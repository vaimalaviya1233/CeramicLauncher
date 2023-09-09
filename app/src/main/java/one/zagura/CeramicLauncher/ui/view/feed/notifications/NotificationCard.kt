package one.zagura.CeramicLauncher.ui.view.feed.notifications

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.provider.notifications.NotificationService
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Gestures
import one.zagura.CeramicLauncher.ui.view.feed.Feed
import one.zagura.CeramicLauncher.ui.view.feed.FeedSection
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager

class NotificationCard : CardView, FeedSection {

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet?) : super(c, a)
    constructor(c: Context, a: AttributeSet?, sa: Int) : super(c, a, sa)

    private val notificationAdapter = NotificationAdapter()

    private val notifications = RecyclerView(context).apply {
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(context)
        adapter = notificationAdapter
    }


    private val arrowUp = ImageView(context).apply {
        setImageResource(R.drawable.arrow_up)
        visibility = GONE
        imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
    }

    private val parentNotificationTitle = TextView(context).apply {
        textSize = 17f
        gravity = Gravity.CENTER_VERTICAL
    }

    private val parentNotificationIcon = ImageView(context).apply {
        setImageResource(R.drawable.ic_notification)
        run {
            val p = 4.dp.toPixels(context)
            setPaddingRelative(p, p, p, p)
        }
    }

    private val parentNotification = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        run {
            val v = 12.dp.toPixels(context)
            setPadding(v, 0, v, 0)
        }

        addView(arrowUp, LayoutParams(MATCH_PARENT, 48.dp.toPixels(context)))
        addView(parentNotificationTitle, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        addView(parentNotificationIcon, LayoutParams(32.dp.toPixels(context), 32.dp.toPixels(context)))

        setOnLongClickListener(Gestures::onLongPress)
        setOnClickListener {
            if (notifications.visibility == VISIBLE)
                collapse() else expand()
        }
    }

    private val linearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        addView(parentNotification, LayoutParams(MATCH_PARENT, 56.dp.toPixels(context)))
        addView(notifications, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    init {
        cardElevation = 0f
        addView(linearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    fun collapse() {
        val firstScroll = feed.scroll.scrollY
        val firstMaxScroll = feed.desktopContent.height
        notifications.visibility = GONE
        arrowUp.visibility = GONE
        parentNotification.background = null
        feed.scroll.post {
            feed.scrollUpdate(firstScroll, firstMaxScroll)
        }
    }

    fun expand() {
        val firstScroll = feed.scroll.scrollY
        val firstMaxScroll = feed.desktopContent.height
        notifications.visibility = VISIBLE
        arrowUp.visibility = VISIBLE
        parentNotification.setBackgroundColor(Settings["notif:text_color", -0xdad9d9] and 0xffffff or 0x22000000)
        feed.scroll.post {
            feed.scrollUpdate(firstScroll, firstMaxScroll)
        }
    }

    var isCollapsingEnabled = true
        set(value) {
            field = value
            if (value) {
                collapse()
                parentNotification.visibility = VISIBLE
            } else {
                expand()
                parentNotification.visibility = GONE
            }
        }

    private lateinit var feed: Feed

    override fun onAdd(feed: Feed, i: Int) {
        this.feed = feed
    }

    fun update() {
        if (Settings["notif:collapse", false]) {
            if (NotificationService.notificationsAmount > 1) {
                parentNotification.visibility = VISIBLE
                parentNotificationTitle.text = resources.getQuantityString(
                    R.plurals.num_notifications,
                    NotificationService.notificationsAmount,
                    NotificationService.notificationsAmount
                )
                if (notifications.visibility == VISIBLE) {
                    parentNotification.setBackgroundColor(Settings["notif:text_color", -0xdad9d9] and 0xffffff or 0x22000000)
                    arrowUp.visibility = VISIBLE
                } else {
                    parentNotification.background = null
                    arrowUp.visibility = GONE
                }
            } else {
                parentNotification.visibility = GONE
                notifications.visibility = VISIBLE
            }
        }
        notificationAdapter.update(NotificationService.notifications)
    }

    override fun updateTheme(activity: Activity) {
        val marginX = Settings["notif:margin_x", 16].dp.toPixels(context)
        val marginY = Settings["feed:card_margin_y", 9].dp.toPixels(context)
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            leftMargin = marginX
            rightMargin = marginX
            topMargin = marginY
            bottomMargin = marginY
        }
        radius = Settings["notif:radius", 8].dp.toFloatPixels(context)
        setCardBackgroundColor(Settings["notif:background_color", -0x1])
        parentNotificationTitle.setTextColor(Settings["notif:title_color", -0xeeeded])
        arrowUp.imageTintList = ColorStateList.valueOf(Settings["notif:text_color", -0xdad9d9])
        parentNotificationIcon.imageTintList = ColorStateList.valueOf(Global.accentColor)
        isCollapsingEnabled = Settings["notif:collapse", false] && NotificationService.notificationsAmount > 1
        val restAtBottom = Settings["feed:rest_at_bottom", false]
        (notifications.layoutManager as LinearLayoutManager).apply {
            reverseLayout = restAtBottom
        }
        linearLayout.removeView(parentNotification)
        linearLayout.addView(parentNotification, if (restAtBottom) 1 else 0)
    }

    override fun onPause() {
        if (Settings["notif:collapse", false] && NotificationService.notificationsAmount > 1) {
            collapse()
        }
    }

    override fun toString() = "notifications"
}