package posidon.launcher.customizations.settingScreens

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import io.posidon.android.conveniencelib.drawable.MaskedDrawable
import io.posidon.android.conveniencelib.drawable.toBitmap
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import posidon.launcher.Global
import posidon.launcher.R
import posidon.launcher.customizations.IconPackPicker
import posidon.launcher.drawable.FastColorDrawable
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Tools
import posidon.launcher.tools.theme.Icons
import posidon.launcher.view.Spinner
import posidon.launcher.view.setting.ColorSettingView


class CustomTheme : AppCompatActivity() {

    private val icShapeViews by lazy { arrayOf<View>(findViewById(R.id.icshapedef), findViewById(R.id.icshaperound), findViewById(R.id.icshaperoundrect), findViewById(R.id.icshaperect), findViewById(R.id.icshapesquircle)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(false)
        else window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        findViewById<View>(R.id.settings).setPadding(0, 0, 0, Tools.navbarHeight)
        window.setBackgroundDrawable(FastColorDrawable(Global.getBlackAccent()))
        val pm = packageManager

        findViewById<TextView>(R.id.icShapeTxt).setTextColor(Global.accentColor)
        findViewById<TextView>(R.id.iconpack_selector).run {
            try { text = pm.getApplicationLabel(pm.getApplicationInfo(Settings["iconpack", "system"], 0)) }
            catch (e: PackageManager.NameNotFoundException) { e.printStackTrace() }
        }

        TextViewCompat.setCompoundDrawableTintList(findViewById(R.id.background_type_label), ColorStateList.valueOf(Global.getPastelAccent()))

        findViewById<ColorSettingView>(R.id.accentColorSetting).onSelected = {
            Global.accentColor = it
            Global.customized = true
        }

        findViewById<Spinner>(R.id.iconBackgrounds).apply {
            data = resources.getStringArray(R.array.iconBackgrounds)
            selectionI = when (Settings["icon:background_type", "custom"]) {
                "dominant" -> 0
                "lv" -> 1
                "dv" -> 2
                "lm" -> 3
                "dm" -> 4
                else -> 5
            }
            setSelectionChangedListener {
                Settings["icon:background_type"] = when (findViewById<Spinner>(R.id.iconBackgrounds).selectionI) {
                    0 -> "dominant"
                    1 -> "lv"
                    2 -> "dv"
                    3 -> "lm"
                    4 -> "dm"
                    else -> "custom"
                }
                Global.shouldSetApps = true
            }
        }

        val size = 48.dp.toPixels(this)
        val bg = BitmapDrawable(resources, FastColorDrawable(Global.accentColor and 0x00ffffff or 0x55000000).toBitmap(size, size))
        bg.setBounds(0, 0, size, size)
        icShapeViews.onEachIndexed { i, v ->
            (v as ViewGroup).addView(ImageView(this).apply {
                val s = Icons.IconShape(i)
                setImageDrawable(if (s.isSquare) bg else MaskedDrawable(bg, s.getPath(size, size)))
            }, 0, ViewGroup.LayoutParams(size, size))
        }
        icShapeViews[Settings["icshape", 4]].setBackgroundResource(R.drawable.selection)
        Global.shouldSetApps = true
    }

    fun iconPackSelector(v: View) = startActivity(Intent(this, IconPackPicker::class.java))

    override fun onResume() {
        super.onResume()
        val pm = packageManager
        val i = findViewById<TextView>(R.id.iconpack_selector)
        val t = findViewById<TextView>(R.id.iconpack_title)
        try {
            val a = pm.getApplicationInfo(Settings["iconpack", "system"], 0)
            i.text = pm.getApplicationLabel(a)
            val icon = a.loadIcon(pm)
            val s = 32.dp.toPixels(this)
            icon.setBounds(0, 0, s, s)
            t.setCompoundDrawablesRelative(icon, null, null, null)
        }
        catch (e: PackageManager.NameNotFoundException) { e.printStackTrace() }
    }

    fun icshapedef(v: View) {
        icShapeViews[Settings["icshape", 4]].setBackgroundColor(0x0)
        Settings["icshape"] = 0
        v.setBackgroundResource(R.drawable.selection)
    }

    fun icshaperound(v: View) {
        icShapeViews[Settings["icshape", 4]].setBackgroundColor(0x0)
        Settings["icshape"] = 1
        v.setBackgroundResource(R.drawable.selection)
    }

    fun icshaperoundrect(v: View) {
        icShapeViews[Settings["icshape", 4]].setBackgroundColor(0x0)
        Settings["icshape"] = 2
        v.setBackgroundResource(R.drawable.selection)
    }

    fun icshaperect(v: View) {
        icShapeViews[Settings["icshape", 4]].setBackgroundColor(0x0)
        Settings["icshape"] = 3
        v.setBackgroundResource(R.drawable.selection)
    }

    fun icshapesquircle(v: View) {
        icShapeViews[Settings["icshape", 4]].setBackgroundColor(0x0)
        Settings["icshape"] = 4
        v.setBackgroundResource(R.drawable.selection)
    }
}