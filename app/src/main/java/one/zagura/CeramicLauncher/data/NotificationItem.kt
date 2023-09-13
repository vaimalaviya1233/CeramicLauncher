package one.zagura.CeramicLauncher.data

import android.app.Notification
import android.app.PendingIntent
import android.graphics.drawable.Drawable
import one.zagura.CeramicLauncher.provider.notifications.NotificationService

class NotificationItem(
    val isConvo: Boolean,
    val title: CharSequence?,
    val text: CharSequence?,
    val source: CharSequence,
    val sourceExtra: CharSequence?,
    val sourceIcon: Drawable?,
    val color: Int,
    val isSummary: Boolean,
    val image: Drawable?,
    private val contentIntent: PendingIntent?,
    val key: String,
    val autoCancel: Boolean,
    val isCancellable: Boolean
) {
    fun open() {
        try {
            contentIntent?.send()
            if (autoCancel) cancel()
        }
        catch (e: Exception) {
            cancel()
            e.printStackTrace()
        }
    }

    fun cancel() {
        NotificationService.instance?.cancelNotification(key)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NotificationItem
        if (title != other.title) return false
        if (isSummary != other.isSummary) return false
        if (key != other.key) return false
        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + isSummary.hashCode()
        result = 31 * result + (contentIntent?.hashCode() ?: 0)
        result = 31 * result + key.hashCode()
        return result
    }
}