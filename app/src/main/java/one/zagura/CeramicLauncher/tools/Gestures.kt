package one.zagura.CeramicLauncher.tools

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.posidon.android.conveniencelib.pullStatusbar
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.customizations.settingScreens.CustomMain
import one.zagura.CeramicLauncher.feed.order.FeedOrderActivity
import one.zagura.CeramicLauncher.items.App
import one.zagura.CeramicLauncher.search.SearchActivity
import one.zagura.CeramicLauncher.storage.Settings

object Gestures {

    const val PULL_DOWN_NOTIFICATIONS = "notif"
    const val OPEN_APP_DRAWER = "drawer"
    const val OPEN_SEARCH = "search"
    const val OPEN_OVERVIEW = "overview"
    const val OPEN_APP = "app"
    const val NOTHING = "nothing"

    fun onLongPress(v: View): Boolean {
        performTrigger(Settings["gesture:long_press", OPEN_OVERVIEW], v.context)
        return true
    }

    fun performTrigger(key: String, context: Context) {
        when (key) {
            PULL_DOWN_NOTIFICATIONS -> context.pullStatusbar()
            OPEN_APP_DRAWER -> Home.instance.drawer.state = BottomSheetBehavior.STATE_EXPANDED
            OPEN_SEARCH -> context.startActivity(Intent(context, SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            OPEN_OVERVIEW -> openOverview(context)
            else -> {
                if (key.startsWith("$OPEN_APP:")) {
                    val string = key.substring(OPEN_APP.length + 1)
                    kotlin.runCatching {
                        App[string]?.open(context, null)
                    }
                }
            }
        }
    }

    fun getIndex(key: String) = when (key) {
        PULL_DOWN_NOTIFICATIONS -> 1
        OPEN_APP_DRAWER -> 2
        OPEN_SEARCH -> 3
        OPEN_OVERVIEW -> 4
        else -> if (key.startsWith(OPEN_APP)) 5 else 0
    }

    fun getKey(i: Int) = when (i) {
        1 -> PULL_DOWN_NOTIFICATIONS
        2 -> OPEN_APP_DRAWER
        3 -> OPEN_SEARCH
        4 -> OPEN_OVERVIEW
        5 -> OPEN_APP
        else -> ""
    }

    object PinchListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(d: ScaleGestureDetector) = true
        override fun onScaleEnd(d: ScaleGestureDetector) = performTrigger(Settings["gesture:pinch", OPEN_OVERVIEW], Tools.appContext!!)
    }

    fun openOverview(context: Context) {
        context.vibrate()
        with(BottomSheetDialog(context, R.style.bottomsheet)) {
            setContentView(R.layout.menu)
            window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.color.ui_card_background)
            findViewById<View>(R.id.custom_button)!!.run {
                setOnClickListener {
                    context.open(CustomMain::class.java, ActivityOptions.makeCustomAnimation(context, R.anim.slideup, R.anim.home_exit).toBundle())
                    this@with.dismiss()
                }
            }
            findViewById<View>(R.id.sections_button)!!.run {
                setOnClickListener {
                    context.open(FeedOrderActivity::class.java, ActivityOptions.makeCustomAnimation(context, R.anim.slideup, R.anim.home_exit).toBundle())
                    this@with.dismiss()
                }
            }
            val tint = ColorStateList.valueOf(Global.getPastelAccent())
            findViewById<ImageView>(R.id.custom_icon)!!.imageTintList = tint
            findViewById<ImageView>(R.id.sections_icon)!!.imageTintList = tint
            show()
        }
    }
}