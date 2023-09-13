package one.zagura.CeramicLauncher.provider.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.palette.graphics.Palette
import io.posidon.android.conveniencelib.AnimUtils
import io.posidon.android.conveniencelib.drawable.toBitmap
import one.zagura.CeramicLauncher.BuildConfig
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.data.MediaPlayerData
import one.zagura.CeramicLauncher.data.NotificationItem
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.provider.media.MediaItemCreator
import one.zagura.CeramicLauncher.util.StackTraceActivity
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Tools
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class NotificationService : NotificationListenerService() {

    override fun onCreate() {
        StackTraceActivity.init(applicationContext)
        Settings.init(applicationContext)
        if (Tools.appContext == null) {
            Tools.appContextReference = WeakReference(applicationContext)
        }
        if (!NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(applicationContext.packageName)) {
            stopSelf()
        }
        val msm = getSystemService(MediaSessionManager::class.java)
        msm.addOnActiveSessionsChangedListener(::onMediaControllersUpdated, componentName)
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        val msm = getSystemService(MediaSessionManager::class.java)
        msm.removeOnActiveSessionsChangedListener(::onMediaControllersUpdated)
    }

    override fun onListenerConnected() {
        loadNotifications(activeNotifications)
    }

    private fun onMediaControllersUpdated(controllers: MutableList<MediaController>?) {
        val old = mediaItem
        if (controllers.isNullOrEmpty()) {
            mediaItem = null
            if (old != null) {
                onMediaUpdate(null)
            }
            return
        }
        val controller = pickController(controllers)
        mediaItem = controller.metadata?.let { MediaItemCreator.create(applicationContext, controller, it) }
        if (old != mediaItem) {
            onMediaUpdate(mediaItem)
        }
        controller.registerCallback(object : MediaController.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadata?) {
                mediaItem = metadata?.let { MediaItemCreator.create(applicationContext, controller, it) }
                onMediaUpdate(mediaItem)
            }
        })
    }

    fun updateMediaItem(context: Context) {
        val msm = context.getSystemService(MediaSessionManager::class.java)
        onMediaControllersUpdated(msm.getActiveSessions(componentName))
    }

    override fun onNotificationPosted(s: StatusBarNotification) = loadNotifications(activeNotifications)
    override fun onNotificationPosted(s: StatusBarNotification?, rm: RankingMap?) = loadNotifications(activeNotifications)
    override fun onNotificationRemoved(s: StatusBarNotification) = loadNotifications(activeNotifications)
    override fun onNotificationRemoved(s: StatusBarNotification?, rm: RankingMap?) = loadNotifications(activeNotifications)
    override fun onNotificationRemoved(s: StatusBarNotification, rm: RankingMap, reason: Int) = loadNotifications(activeNotifications)
    override fun onNotificationRankingUpdate(rm: RankingMap) = loadNotifications(activeNotifications)
    override fun onNotificationChannelModified(pkg: String, u: UserHandle, c: NotificationChannel, modifType: Int) = loadNotifications(activeNotifications)
    override fun onNotificationChannelGroupModified(pkg: String, u: UserHandle, g: NotificationChannelGroup, modifType: Int) = loadNotifications(activeNotifications)

    private fun loadNotifications(notifications: Array<StatusBarNotification>?) {
        thread (name = "NotificationService loading thread", isDaemon = true) {
            Settings.init(applicationContext)

            fun showNotificationBadgeOnPackage(packageName: String) {
                App.getFromPackage(packageName)
                    ?.forEach { it.notificationCount++ }
            }

            val tmpNotifications = ArrayList<NotificationItem>()
            var i = 0
            var notificationsAmount2 = 0
            lock.withLock {
                try {
                    for (app in Global.apps) {
                        app.notificationCount = 0
                    }
                    if (notifications != null) {
                        while (i < notifications.size) {
                            val notification = notifications[i]

                            if (!notification.isClearable && Settings["notif:hide_persistent", false]) {
                                i++
                                continue
                            }

                            if (notification.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) {
                                i++
                                continue
                            }

                            if (
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                                && notification.notification.bubbleMetadata?.isNotificationSuppressed == true
                            ) {
                                i++
                                continue
                            }

                            if (Settings["notif:ex:${notification.packageName}", false]) {
                                i++
                                continue
                            }

                            val isMusic = notification.notification.extras
                                .getCharSequence(Notification.EXTRA_TEMPLATE) == Notification.MediaStyle::class.java.name

                            if (isMusic) {
                                i++
                                continue
                            }

                            showNotificationBadgeOnPackage(notifications[i].packageName)
                            tmpNotifications.add(
                                NotificationCreator.create(applicationContext, notifications[i]))
                            notificationsAmount2++
                            i++
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: OutOfMemoryError) {
                    tmpNotifications.clear()
                    Companion.notifications.clear()
                    notificationsAmount2 = 0
                }
                Companion.notifications = tmpNotifications
                notificationsAmount = notificationsAmount2
                onUpdate()
            }
        }
    }

    companion object {

        private val componentName = ComponentName(BuildConfig.APPLICATION_ID, NotificationService::class.java.name)

        var instance: NotificationService? = null
            private set

        var notifications = ArrayList<NotificationItem>()
            private set

        var onUpdate = {}

		var notificationsAmount = 0
        private val lock = ReentrantLock()

        var mediaItem: MediaPlayerData? = null
            private set

        var onMediaUpdate: (MediaPlayerData?) -> Unit = {}

        private fun pickController(controllers: List<MediaController>): MediaController {
            for (i in controllers.indices) {
                val mc = controllers[i]
                if (mc.playbackState?.state == PlaybackState.STATE_PLAYING) {
                    return mc
                }
            }
            return controllers[0]
        }
    }
}