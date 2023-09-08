package one.zagura.CeramicLauncher.provider.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import one.zagura.CeramicLauncher.data.NotificationItem
import one.zagura.CeramicLauncher.util.storage.Settings

object NotificationCreator {

    inline fun getIcon(context: Context, n: StatusBarNotification): Drawable? {
        return n.notification.smallIcon?.loadDrawable(context)
    }

    inline fun getSource(context: Context, n: StatusBarNotification): String {
        return context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(n.packageName, 0)).toString()
    }

    inline fun getColor(n: StatusBarNotification): Int {
        return n.notification.color
    }

    inline fun getTitle(extras: Bundle): CharSequence? {
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)
        if (title == null || title.toString().replace(" ", "").isEmpty()) {
            return null
        }
        return title
    }

    inline fun getText(extras: Bundle): CharSequence? {
        val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
        return if (messages == null) {
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?: extras.getCharSequence(Notification.EXTRA_TEXT)
        } else buildString {
            messages.forEach {
                val bundle = it as Bundle
                appendLine(bundle.getCharSequence("text"))
            }
            delete(lastIndex, length)
        }
    }

    inline fun getImage(context: Context, extras: Bundle, notification: Notification, messagingStyle: NotificationCompat.MessagingStyle?): Drawable? {
        val b = extras[Notification.EXTRA_PICTURE] as Bitmap?
        if (b != null) {
            return BitmapDrawable(context.resources, b)
        }
        messagingStyle?.messages?.asReversed()?.forEach {
            it.dataUri?.let { uri ->
                runCatching {
                    return Drawable.createFromStream(context.contentResolver.openInputStream(uri), null)
                }
            }
        }
        return notification.getLargeIcon()?.loadDrawable(context)
    }

    inline fun getImportance(importance: Int): Int {
        return when (importance) {
            NotificationManager.IMPORTANCE_NONE,
            NotificationManager.IMPORTANCE_MIN -> -1
            NotificationManager.IMPORTANCE_LOW,
            NotificationManager.IMPORTANCE_DEFAULT -> 0
            NotificationManager.IMPORTANCE_HIGH -> 1
            NotificationManager.IMPORTANCE_MAX -> 2
            else -> throw IllegalStateException("Invalid notification importance")
        }
    }

    fun create(context: Context, notification: StatusBarNotification): NotificationItem {

        val extras = notification.notification.extras

        var title = getTitle(extras)
        var text = getText(extras)
        if (title == null) {
            title = text
            text = null
        }
        val color = getColor(notification)
        val isSummary = notification.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0
        val source = getSource(context, notification)
        val sourceIcon = getIcon(context, notification)?.apply {
            this.setTintList(ColorStateList.valueOf(
                if (
                    color == Settings["notif:background_color", -0x1] ||
                    color == 0
                ) Settings["notif:title_color", -0xeeeded] else color
            ))
        }

        //println(extras.keySet().joinToString("\n") { "$it -> " + extras[it].toString() })

        val progress = extras.getInt(Notification.EXTRA_PROGRESS, -1)
        val maxProgress = extras.getInt(Notification.EXTRA_PROGRESS_MAX, -1)
        val intermediate = extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false)

        val autoCancel = notification.notification.flags and Notification.FLAG_AUTO_CANCEL != 0

        val messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
            notification.notification)
        if (messagingStyle != null) {
            messagingStyle.conversationTitle?.toString()?.let { title = it }
            messagingStyle.messages.takeLast(3).mapNotNull { it.text }.ifEmpty { null }?.joinToString("\n")?.let { text = it }
        }

        val image = if (isSummary) null else getImage(context, extras, notification.notification, messagingStyle)

        return NotificationItem(
            title = title,
            text = text,
            source = source,
            sourceIcon = sourceIcon,
            color = color,
            isSummary = isSummary,
            image = image,
            actions = notification.notification.actions,
            contentIntent = notification.notification.contentIntent,
            key = notification.key,
            progress = if (intermediate) -2 else progress,
            max = if (intermediate) 0 else maxProgress,
            autoCancel = autoCancel,
            isCancellable = notification.isClearable,
        )
    }
}