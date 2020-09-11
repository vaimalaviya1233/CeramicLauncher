package posidon.launcher.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import posidon.launcher.R
import posidon.launcher.tools.dp

class HeaderSettingView : FrameLayout {

    private lateinit var labelView: TextView

    constructor(c: Context) : this(c, null)
    constructor(c: Context, a: AttributeSet?) : this(c, a, 0)
    constructor(c: Context, a: AttributeSet?, sa: Int) : this(c, a, sa, 0)
    constructor(c: Context, a: AttributeSet?, sa: Int, sr: Int) : super(c, a, sa, sr) {
        init(a, sa, sr)
    }

    fun init(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)

        labelView = TextView(context).apply {
            text = a.getString(R.styleable.SettingView_label)
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
            includeFontPadding = false
            val p = 20.dp.toInt()
            setPadding(p, p, p, p)
            setTextColor(context.resources.getColor(R.color.cardtitle))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL)
        }
        addView(labelView)
        val separator = View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 2.dp.toInt(), Gravity.BOTTOM)
            setBackgroundResource(R.drawable.card_separator)
        }
        addView(separator)

        a.recycle()
    }
}