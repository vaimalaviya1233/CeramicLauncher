package posidon.launcher.view.setting

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import posidon.launcher.R
import posidon.launcher.storage.Settings
import posidon.launcher.tools.ColorTools
import posidon.launcher.tools.dp
import kotlin.math.max
import kotlin.math.min

class ColorSettingView : IntSettingView {

    private lateinit var colorPreview: View

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : super(c, a)
    constructor(c: Context, a: AttributeSet, sa: Int) : super(c, a, sa)
    constructor(c: Context, a: AttributeSet, sa: Int, sr: Int) : super(c, a, sa, sr)

    var onSelected: ((color: Int) -> Unit)? = null

    override fun populate(attrs: AttributeSet?, defStyle: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ColorSettingView, defStyle, 0)

        val hasAlpha = a.getBoolean(R.styleable.ColorSettingView_hasAlpha, true)
        colorPreview = View(context).apply {
            val size = 40.dp.toInt()
            layoutParams = LayoutParams(size, size, 0f).apply {
                val m = 10.dp.toInt()
                setMargins(m, m, m, m)
            }
        }
        setPreviewColor(Settings[key, default] or -0x1000000)
        addView(colorPreview)
        val pickColor = if (hasAlpha) {
            ColorTools::pickColor
        } else {
            ColorTools::pickColorNoAlpha
        }
        setOnClickListener {
            val c = Settings[key, default]
            pickColor(context, if (hasAlpha) c else c and 0xffffff) {
                val color = if (hasAlpha) it else it or -0x1000000
                setPreviewColor(color)
                Settings[key] = color
                onSelected?.invoke(color)
            }
        }
    }

    private fun setPreviewColor(it: Int) {
        colorPreview.background = ColorTools.colorCircle(it)
        val hsv = FloatArray(3)
        Color.colorToHSV(it, hsv)
        hsv[1] = min(hsv[1],0.5f)
        hsv[2] = min(max(0.6f, hsv[2]), 0.9f)
        val pastel = Color.HSVToColor(hsv)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            labelView.compoundDrawableTintList = ColorStateList.valueOf(pastel)
        }
    }
}