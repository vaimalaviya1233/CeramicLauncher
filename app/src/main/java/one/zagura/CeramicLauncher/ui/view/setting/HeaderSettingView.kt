package one.zagura.CeramicLauncher.ui.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R

open class HeaderSettingView : FrameLayout {

    private lateinit var labelView: TextView

    constructor(c: Context) : this(c, null)
    constructor(c: Context, a: AttributeSet?) : this(c, a, 0)
    constructor(c: Context, a: AttributeSet?, sa: Int) : this(c, a, sa, 0)
    constructor(c: Context, a: AttributeSet?, sa: Int, sr: Int) : super(c, a, sa, sr) {
        init(a, sa, sr)
    }

    var label: CharSequence
        get() = labelView.text
        set(value) {
            labelView.text = value
        }

    protected fun init(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)

        val separator = View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.dp.toPixels(context))
            setBackgroundResource(R.drawable.ui_separator)
        }
        addView(separator)

        labelView = TextView(context).apply {
            text = a.getString(R.styleable.SettingView_label)
            textSize = 18f
            includeFontPadding = false
            val p = 16.dp.toPixels(context)
            setPaddingRelative(24.dp.toPixels(context), p, p, p)
            setTextColor(Global.getPastelAccent())
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        addView(labelView)

        populate(attrs, defStyle, defStyleRes)

        a.recycle()
    }

    protected open fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {}
}