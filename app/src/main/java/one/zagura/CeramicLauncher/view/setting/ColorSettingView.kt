package one.zagura.CeramicLauncher.view.setting

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.TextViewCompat
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.tools.theme.ColorTools
import kotlin.math.max
import kotlin.math.min

class ColorSettingView : IntSettingView {

    private lateinit var colorPreview: View
    private var hasAlpha = true

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : this(c, a, 0)
    constructor(c: Context, a: AttributeSet, sa: Int) : this(c, a, sa, 0)
    constructor(c: Context, attrs: AttributeSet, sa: Int, sr: Int) : super(c, attrs, sa, sr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ColorSettingView, sa, sr)
        hasAlpha = a.getBoolean(R.styleable.ColorSettingView_hasAlpha, true)
        a.recycle()
        run {
            val d = Settings[key, default]
            setPreviewColor(if (hasAlpha) d else d or -0x1000000)
        }
    }

    constructor(c: Context, key: String, default: Int, labelId: Int, iconId: Int, hasAlpha: Boolean) : super(c, key, default, labelId, iconId) {
        this.hasAlpha = hasAlpha
        run {
            val d = Settings[key, default]
            setPreviewColor(if (hasAlpha) d else d or -0x1000000)
        }
    }

    fun setPreviewColor(it: Int) {
        colorPreview.background = ColorTools.colorPreview(it)
        val hsv = FloatArray(3)
        Color.colorToHSV(it, hsv)
        hsv[1] = min(hsv[1],0.5f)
        hsv[2] = min(max(0.4f, hsv[2]), 0.75f)
        val pastel = Color.HSVToColor(hsv)
        TextViewCompat.setCompoundDrawableTintList(labelView, ColorStateList.valueOf(pastel))
    }

    var onSelected: ((color: Int) -> Unit)? = null

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        colorPreview = View(context)
        val size = 36.dp.toPixels(context)
        addView(colorPreview, LayoutParams(size, size, 0f).apply {
            val m = 12.dp.toPixels(context)
            setMargins(m, m, m, m)
        })
        setOnClickListener {
            val c = Settings[key, default].let { if (hasAlpha) it else it and 0xffffff }
            (if (hasAlpha)
                ColorTools::pickColor
            else ColorTools::pickColorNoAlpha)(context, c) {
                val color = if (hasAlpha) it else it or -0x1000000
                setPreviewColor(color)
                Settings[key] = color
                onSelected?.invoke(color)
            }
        }
    }
}