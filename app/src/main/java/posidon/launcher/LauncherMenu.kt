package posidon.launcher

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.PathInterpolator
import posidon.launcher.customizations.settingScreens.Customizations
import posidon.launcher.external.Kustom
import posidon.launcher.feed.order.FeedOrderActivity
import posidon.launcher.storage.Settings
import posidon.launcher.tools.*
import posidon.launcher.tools.theme.Wallpaper
import posidon.launcher.view.drawer.BottomDrawerBehavior
import posidon.launcher.view.drawer.DrawerView
import posidon.launcher.wall.Gallery

object LauncherMenu {

    var isActive = false
    var dialog: Dialog? = null

    fun openOverview(home: Activity) {
        if (!isActive) {
            val window = home.window
            isActive = true
            home.vibrate()
            val homescreen = window.decorView.findViewById<View>(android.R.id.content)
            val scrollbarPosition = Settings["drawer:scrollbar:position", 1]
            val scrollbarWidth = if (Settings["drawer:scrollbar:enabled", false] && Settings["drawer:scrollbar:show_outside", false]) {
                if (scrollbarPosition == 0) {
                    -Settings["drawer:scrollbar:width", 24].dp
                }
                else Settings["drawer:scrollbar:width", 24].dp
            } else 0f
            val page = homescreen.findViewById<View>(R.id.feed)
            val drawer = homescreen.findViewById<DrawerView>(R.id.drawer)
            page.animate().apply {
                if (scrollbarPosition != 2)
                    translationX(scrollbarWidth / 2f)
            }.scaleX(0.65f).scaleY(0.65f).translationY(page.height * -0.05f).setInterpolator(PathInterpolator(0.245f, 1.275f, 0.405f, 1.005f)).duration = 450L
            drawer.scrollBar.animate().apply {
                if (scrollbarPosition != 2)
                    translationX(scrollbarWidth)
            }.duration = 100L
            drawer.state = BottomDrawerBehavior.STATE_HIDDEN
            dialog = Dialog(home, R.style.longpressmenusheet)
            dialog!!.setContentView(R.layout.menu)
            dialog!!.window!!.setGravity(Gravity.BOTTOM)
            dialog!!.findViewById<View>(R.id.custombtn).setOnClickListener {
                home.open(Customizations::class.java, ActivityOptions.makeCustomAnimation(home, R.anim.slideup, R.anim.home_exit).toBundle())
                dialog!!.dismiss()
            }
            dialog!!.findViewById<View>(R.id.wallbtn).setOnClickListener {
                home.open(Gallery::class.java, ActivityOptions.makeCustomAnimation(home, R.anim.slideup, R.anim.home_exit).toBundle())
                dialog!!.dismiss()
            }
            dialog!!.findViewById<View>(R.id.sectionsBtn).setOnClickListener {
                home.open(FeedOrderActivity::class.java, ActivityOptions.makeCustomAnimation(home, R.anim.slideup, R.anim.home_exit).toBundle())
                dialog!!.dismiss()
            }
            page.setBackgroundResource(R.drawable.page)
            if (Tools.canBlurDrawer) {
                window.setBackgroundDrawable(LayerDrawable(arrayOf(BitmapDrawable(home.resources, Wallpaper.blurredWall(Settings["drawer:blur:rad", 15f])), home.getDrawable(R.drawable.black_gradient))))
            } else {
                window.setBackgroundDrawableResource(R.drawable.black_gradient)
            }
            homescreen.setOnClickListener { dialog!!.dismiss() }
            dialog!!.setOnDismissListener { exit(homescreen, window, drawer) }
            dialog!!.show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                homescreen.systemGestureExclusionRects = listOf()
            }
            if (Settings["kustom:variables:enable", false]) {
                Kustom["screen"] = "overview"
            }
        }
    }

    private fun exit(homescreen: View, window: Window, drawer: DrawerView) {
        drawer.scrollBar.animate().translationX(0f).translationY(0f)
        drawer.state = BottomDrawerBehavior.STATE_COLLAPSED
        val page = homescreen.findViewById<View>(R.id.feed)
        page.animate().translationX(0f).scaleX(1f).scaleY(1f).translationY(0f).duration = 400L
        page.setBackgroundColor(0x0)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Settings["gesture:back", ""] == "") {
            homescreen.systemGestureExclusionRects = listOf(Rect(0, 0, Device.displayWidth, Device.displayHeight))
        }
        isActive = false
        dialog = null
        if (Settings["kustom:variables:enable", false]) {
            Kustom["screen"] = "home"
        }
    }
}