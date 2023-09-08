package one.zagura.CeramicLauncher.ui.view.feed

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.posidon.android.conveniencelib.Device
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.ContactItem
import one.zagura.CeramicLauncher.data.items.LauncherItem
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.groupView.ItemGroupView
import kotlin.concurrent.thread
import kotlin.math.min

class ContactCardView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), FeedSection {

    private val items = ArrayList<LauncherItem>()
    private val gridLayout = GridLayout(context)
    init {
        orientation = VERTICAL
        addView(gridLayout, LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    var columns
        get() = gridLayout.columnCount
        set(value) {
            gridLayout.columnCount = value
        }

    fun setItems(list: Iterable<ContactItem>, parent: ViewParent) {
        clear()
        list.forEachIndexed { i, item ->
            items.add(item)
            gridLayout.addView(getItemView(i, item, parent).apply {
                setOnTouchListener { _, event ->
                    val parent = this@ContactCardView.parent
                    if (parent != null) {
                        val parentContainer = parent as View
                        if (parentContainer.canScrollVertically(-1)) {
                            parentContainer.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    gridLayout.onTouchEvent(event)
                    false
                }
            })
        }
    }

    fun clear() {
        items.clear()
        gridLayout.removeAllViews()
    }

    fun getItemView(i: Int, item: ContactItem, parent: ViewParent): View {
        return (LayoutInflater.from(context).inflate(R.layout.floating_item, gridLayout, false)).apply {
            val appSize = min(64.dp.toPixels(context), this@ContactCardView.measuredWidth / columns - 4.dp.toPixels(context) * 2)
            findViewById<ImageView>(R.id.iconimg).setImageDrawable(item.icon)
            findViewById<View>(R.id.iconimg).run {
                layoutParams.height = appSize
                layoutParams.width = appSize
            }
            with(findViewById<TextView>(R.id.icontxt)) {
                text = item.label
                backgroundTintList = ColorStateList.valueOf(Settings["contacts_card:bg_color", 0xffffffff.toInt()])
                setTextColor(Settings["contacts_card:text_color", 0xff252627.toInt()])
            }
            setOnClickListener { v -> item.open(context, v, -1) }
            (layoutParams as GridLayout.LayoutParams).bottomMargin = if (i / columns == 0) 0 else Settings["verticalspacing", 12].dp.toPixels(context)
        }
    }

    override fun updateTheme(activity: Activity) {
        val marginX = Settings["feed:card_margin_x", 16].dp.toPixels(context)
        val marginY = Settings["feed:card_margin_y", 9].dp.toPixels(context)
        columns = Settings["contacts_card:columns", 5]
        (layoutParams as MarginLayoutParams).run {
            leftMargin = marginX
            rightMargin = marginX
            topMargin = marginY
            bottomMargin = marginY
        }
    }

    override fun onResume(activity: Activity) {
        thread (isDaemon = true) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ContactItem.getList(true).also {
                    activity.runOnUiThread {
                        setItems(it, parent)
                    }
                }
            }
        }
    }

    override fun toString() = "starred_contacts"
}