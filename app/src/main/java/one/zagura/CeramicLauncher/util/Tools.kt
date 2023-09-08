package one.zagura.CeramicLauncher.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.UserHandle
import android.os.UserManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.Device
import io.posidon.android.conveniencelib.getNavigationBarHeight
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import io.posidon.android.conveniencelib.vibrate
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.recycler.GridLayoutManager
import java.lang.ref.WeakReference
import java.util.Locale
import kotlin.math.abs

object Tools {

    inline fun isInstalled(packageName: String, packageManager: PackageManager): Boolean {
        var found = true
        try { packageManager.getPackageInfo(packageName, 0) }
        catch (e: Exception) { found = false }
        return found
    }

    var appContextReference = WeakReference<Context>(null)
    inline val appContext get() = appContextReference.get()

	var navbarHeight = 0

	fun updateNavbarHeight(activity: Activity) {
        if (Settings["ignore_navbar", false]) {
            navbarHeight = 0
            return
        }
        navbarHeight = activity.getNavigationBarHeight()
    }

    fun isDefaultLauncher(packageManager: PackageManager): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val defLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.resolvePackageName
        return defLauncher == "one.zagura.CeramicLauncher"
    }

    fun searchOptimize(s: String) = s.lowercase(Locale.getDefault())
        .replace('ñ', 'n')
        .replace('e', '3')
        .replace('a', '4')
        .replace('i', '1')
        .replace('¿', '?')
        .replace('¡', '!')
        .replace("wh", "w")
        .replace(Regex("(k|cc|ck)"), "c")
        .replace(Regex("(z|ts|sc|cs|tz)"), "s")
        .replace(Regex("([-'&/_,.:;*\"]|gh)"), "")

    private class AppSelectionAdapter(
        val apps: List<App>,
        val onClick: (app: App) -> Unit
    ) : RecyclerView.Adapter<AppSelectionAdapter.ViewHolder>() {

        class ViewHolder(
            val view: View,
            val icon: ImageView,
            val text: TextView
        ) : RecyclerView.ViewHolder(view)

        override fun getItemCount() = apps.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(appContext!!).inflate(R.layout.drawer_item, parent, false)
            val icon = view.findViewById<ImageView>(R.id.iconimg)
            val text = view.findViewById<TextView>(R.id.icontxt)
            return ViewHolder(view, icon, text)
        }

        override fun onBindViewHolder(holder: ViewHolder, i: Int) {
            val app = apps[i]
            holder.icon.setImageDrawable(app.icon)
            holder.text.text = app.label
            holder.view.setOnClickListener {
                onClick(app)
            }
        }
    }

    fun selectApp(context: Context, includeHidden: Boolean, out: (app: App) -> Unit) = Dialog(context, R.style.longpressmenusheet).run {
        setContentView(RecyclerView(context).apply {
            val apps = if (includeHidden) ArrayList<App>().apply {
                addAll(Global.apps)
                addAll(App.hidden)
                if (Settings["drawer:sorting", 0] == 1) sortWith { o1, o2 ->
                    val iHsv = floatArrayOf(0f, 0f, 0f)
                    val jHsv = floatArrayOf(0f, 0f, 0f)
                    Color.colorToHSV(Palette.from(o1.icon!!.toBitmap()).generate().getVibrantColor(0xff252627.toInt()), iHsv)
                    Color.colorToHSV(Palette.from(o2.icon!!.toBitmap()).generate().getVibrantColor(0xff252627.toInt()), jHsv)
                    (iHsv[0] - jHsv[0]).toInt()
                }
                else sortWith { o1, o2 ->
                    o1.label.compareTo(o2.label, ignoreCase = true)
                }
            } else Global.apps
            layoutManager = GridLayoutManager(context, 4)
            adapter = AppSelectionAdapter(apps) {
                out(it)
                dismiss()
            }
        }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenHeight(context) - 300.dp.toPixels(context)))
        window!!.setBackgroundDrawableResource(R.drawable.card)
        show()
    }

    inline fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val threshold = 16.dp.toPixels(appContext!!)
        return abs(startX - endX) < threshold && abs(startY - endY) < threshold
    }

    /**
     * @return Triple(x, y, gravity)
     */
    inline fun getPopupLocationFromView(view: View): Triple<Int, Int, Int> {

        val location = IntArray(2).also {
            view.getLocationOnScreen(it)
        }

        var gravity: Int

        val screenWidth = Device.screenWidth(view.context)
        val screenHeight = Device.screenHeight(view.context)

        val x = if (location[0] > screenWidth / 2) {
            gravity = Gravity.END
            screenWidth - location[0] - view.measuredWidth
        } else {
            gravity = Gravity.START
            location[0]
        }

        val y = if (location[1] < screenHeight / 2) {
            gravity = gravity or Gravity.TOP
            location[1] + view.measuredHeight + 4.dp.toPixels(appContext!!)
        } else {
            gravity = gravity or Gravity.BOTTOM
            screenHeight - location[1] + 4.dp.toPixels(appContext!!) + navbarHeight
        }

        return Triple(x, y, gravity)
    }
}

inline fun Context.vibrate() = vibrate(Settings["hapticfeedback", 14])

inline fun Context.open(action: String, b: Bundle? = null, block: Intent.() -> Unit) = startActivity(Intent(action)
    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK).also(block), b)
inline fun Context.open(c: Class<*>, b: Bundle? = null, block: Intent.() -> Unit) = startActivity(Intent(this, c)
    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK).also(block), b)
inline fun Context.open(action: String, b: Bundle? = null) = startActivity(Intent(action)
    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK), b)
inline fun Context.open(c: Class<*>, b: Bundle? = null) = startActivity(Intent(this, c)
    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK), b)


inline fun Drawable.clone() = constantState?.newDrawable()?.mutate()

inline fun Drawable.toBitmapDrawable(duplicateIfBitmapDrawable: Boolean = false) = if (this is BitmapDrawable && !duplicateIfBitmapDrawable) this else {
    BitmapDrawable(Tools.appContext!!.resources, toBitmap())
}

fun Context.isUserRunning(profile: UserHandle): Boolean =
    getSystemService(UserManager::class.java).isUserRunning(profile)