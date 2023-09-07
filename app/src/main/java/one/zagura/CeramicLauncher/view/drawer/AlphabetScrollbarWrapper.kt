package one.zagura.CeramicLauncher.view.drawer

import android.annotation.SuppressLint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.posidon.android.conveniencelib.Device
import io.posidon.android.conveniencelib.getStatusBarHeight
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.tools.Tools

class AlphabetScrollbarWrapper(
    listView: AbsListView,
    @Companion.Orientation
    orientation: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlphabetScrollbar(listView, orientation, attrs, defStyleAttr) {

    /**
     * @return The space the dock should reserve for the scrollbar (if it's horizontal)
     */
    @SuppressLint("RtlHardcoded")
    fun updateTheme(drawer: DrawerView, feed: View, dock: DockView): Int {
        (parent as ViewGroup?)?.removeView(this)
        if (!Settings["drawer:scrollbar:enabled", false]) {
            visibility = GONE
            feed.layoutParams = (feed.layoutParams as ViewGroup.MarginLayoutParams).apply { leftMargin = 0; rightMargin = 0 }
            drawer.drawerGrid.layoutParams = (drawer.drawerGrid.layoutParams as ViewGroup.MarginLayoutParams).apply { leftMargin = 0; rightMargin = 0 }
            return 0
        }
        var ret = 0
        val scrollbarWidth = Settings["drawer:scrollbar:width", 24].dp.toPixels(context)
        val reserveSpace = Settings["drawer:scrollbar:reserve_space", true]
        val position = Settings["drawer:scrollbar:position", 1]
        val isHorizontal = position == 2
        visibility = VISIBLE
        textColor = Settings["drawer:scrollbar:text_color", 0xaaeeeeee.toInt()]
        highlightColor = Settings["drawer:scrollbar:highlight_color", 0xffffffff.toInt()]
        floatingColor = Settings["drawer:scrollbar:floating_color", 0xffffffff.toInt()]
        setBackgroundColor(Settings["drawer:scrollbar:bg_color", 0])
        updateTheme()
        drawer.drawerGrid.layoutParams = (drawer.drawerGrid.layoutParams as ViewGroup.MarginLayoutParams).apply {
            if (isHorizontal) { leftMargin = 0; rightMargin = 0 }
            else {
                val m = if (reserveSpace) scrollbarWidth else 0
                if (position == 0) leftMargin = m
                else rightMargin = if (Settings["drawer:sections:enabled", false]) 0 else m
            }
        }
        orientation = if (isHorizontal) HORIZONTAL else VERTICAL
        if (Settings["drawer:scrollbar:show_outside", false]) {
            if (isHorizontal) {
                Home.instance.homeView.addView(this, CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scrollbarWidth + Tools.navbarHeight).apply {
                    gravity = Gravity.BOTTOM
                })
                val h = 28.dp.toPixels(context)
                setPadding(h, 0, h, Tools.navbarHeight)
                ret = if (reserveSpace) scrollbarWidth else 0
            } else {
                Home.instance.homeView.addView(this, CoordinatorLayout.LayoutParams(scrollbarWidth, Device.screenHeight(context) + Tools.navbarHeight).apply {
                    gravity = (if (position == 0) Gravity.LEFT else Gravity.RIGHT) or Gravity.BOTTOM
                })
                feed.layoutParams = (feed.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    val m = if (reserveSpace) scrollbarWidth else 0
                    if (position == 0) leftMargin = m
                    else rightMargin = m
                }
                setPadding(0, context.getStatusBarHeight() + 12.dp.toPixels(context), 0, dock.dockHeight + Tools.navbarHeight)
            }
            onStartScroll = {
                if (drawer.state != BottomSheetBehavior.STATE_EXPANDED)
                    drawer.state = BottomSheetBehavior.STATE_EXPANDED
            }
            onCancelScroll = { drawer.state = BottomSheetBehavior.STATE_COLLAPSED }
            floatingFactor = 1f
            alpha = 1f
        } else {
            if (isHorizontal) {
                drawer.searchBarVBox.addView(this, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scrollbarWidth))
                val h = 28.dp.toPixels(context)
                setPadding(h, 0, h, 0)
            } else {
                drawer.drawerContent.addView(this, FrameLayout.LayoutParams(
                        scrollbarWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (if (position == 0) Gravity.LEFT else Gravity.RIGHT) or Gravity.TOP
                ))
                feed.layoutParams = (feed.layoutParams as ViewGroup.MarginLayoutParams).apply { leftMargin = 0; rightMargin = 0 }
                setPadding(0, drawer.drawerGrid.paddingTop + context.getStatusBarHeight() + Settings["dockbottompadding", 10].dp.toPixels(context), 0, drawer.drawerGrid.paddingBottom)
            }
            onStartScroll = {}
            onCancelScroll = {}
            floatingFactor = 0f
        }
        bringToFront()
        return ret
    }
}