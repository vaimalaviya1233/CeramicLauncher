package one.zagura.CeramicLauncher.ui.view.feed.notifications

import android.app.RemoteInput
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.luminance
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.NotificationItem
import one.zagura.CeramicLauncher.provider.notifications.NotificationService
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Gestures
import one.zagura.CeramicLauncher.ui.view.SwipeableLayout
import one.zagura.CeramicLauncher.util.theme.ColorTools

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val card: SwipeableLayout) : RecyclerView.ViewHolder(card)

    private var notifications = ArrayList<NotificationItem>()

    override fun getItemCount() = notifications.size

    override fun getItemViewType(i: Int): Int =
        if (notifications[i].isConvo) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val context = parent.context

        val view = LayoutInflater.from(context).inflate(if (type == 1) R.layout.notification_convo else R.layout.notification, null).apply {
            setOnLongClickListener(Gestures::onLongPress)
            val p = context.resources.getDimension(R.dimen.notification_primary_area_padding).toInt()
            setPadding(p, p / 2, p, p / 2)
        }
        val swipeable = SwipeableLayout(view).apply {
            val bg = Settings["notif:card_swipe_bg_color", 0x880d0e0f.toInt()]
            setIconColor(if (bg.luminance > .6f) 0xff000000.toInt() else 0xffffffff.toInt())
            setSwipeColor(bg)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        if (type == 0)
            view.findViewById<TextView>(R.id.source_extra).setTextColor(Settings["notif:text_color", -0xdad9d9])
        view.findViewById<TextView>(R.id.title).setTextColor(Settings["notif:title_color", -0xeeeded])
        with(view.findViewById<TextView>(R.id.txt)) {
            setTextColor(Settings["notif:text_color", -0xdad9d9])
            maxLines = if (type == 1) 3 else 1
        }

        return ViewHolder(swipeable).apply {
            card.onSwipeAway = {
                val n = notifications[adapterPosition]
                if (n.isCancellable) {
                    try { n.cancel() }
                    catch (e: Exception) { e.printStackTrace() }
                } else card.reset()
            }
            view.setOnClickListener { notifications[adapterPosition].open() }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val view = holder.card
        view.reset()

        val notification = notifications[i]

        if (!notification.isConvo) {
            with(view.findViewById<TextView>(R.id.source_extra)) {
                if (notification.sourceExtra == null) {
                    text = null
                    isVisible = false
                } else {
                    isVisible = true
                    text = " • ${notification.sourceExtra}"
                }
            }
        }

        val tintedFGColor =
            ColorTools.makeContrasty(
                notification.color,
                Settings["notif:background_color", -0x1]
            )

        with(view.findViewById<TextView>(R.id.title)) {
            text = notification.title
            setTextColor(if (notification.isConvo && notification.title != null)
                ColorTools.makeContrasty(
                    ColorTools.getColorForText(notification.title),
                    Settings["notif:background_color", -0x1]
                )
            else tintedFGColor)
        }
        with(view.findViewById<TextView>(R.id.txt)) {
            text = notification.text
        }
        with(view.findViewById<ImageView>(R.id.source_icon)) {
            setImageDrawable(notification.sourceIcon)
            if (notification.isConvo) {
                backgroundTintList = ColorStateList.valueOf(tintedFGColor)
                imageTintList = ColorStateList.valueOf(Settings["notif:background_color", -0x1])
            } else imageTintList = ColorStateList.valueOf(tintedFGColor)
        }

        with(view.findViewById<ImageView>(R.id.image)) {
            setImageDrawable(notification.image)
            isVisible = notification.image != null
        }
    }

    fun update(notifications: ArrayList<NotificationItem>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }
}
