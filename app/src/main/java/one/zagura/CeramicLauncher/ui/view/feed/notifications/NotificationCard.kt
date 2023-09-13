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
import androidx.core.view.setPadding
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
        val p = context.resources.getDimension(R.dimen.notification_primary_area_padding).toInt()
        setPadding(0, p / 2, 0, p / 2)
    }

    init {
        cardElevation = 0f
        addView(notifications, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    private lateinit var feed: Feed

    override fun onAdd(feed: Feed, i: Int) {
        this.feed = feed
    }

    fun update() {
        notificationAdapter.update(NotificationService.notifications)
    }

    override fun updateTheme(activity: Activity) {
        val marginX = Settings["notif:margin_x", 0].dp.toPixels(context)
        val marginY = Settings["feed:card_margin_y", 9].dp.toPixels(context)
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            leftMargin = marginX
            rightMargin = marginX
            topMargin = marginY
            bottomMargin = marginY
        }
        radius = Settings["notif:radius", 0].dp.toFloatPixels(context)
        setCardBackgroundColor(Settings["notif:background_color", -0x1])
        val restAtBottom = Settings["feed:rest_at_bottom", false]
        (notifications.layoutManager as LinearLayoutManager).apply {
            reverseLayout = restAtBottom
        }
    }

    override fun toString() = "notifications"
}