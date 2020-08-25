package posidon.launcher.view.setting

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import posidon.launcher.R
import posidon.launcher.tools.dp

abstract class SettingView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    protected lateinit var labelView: TextView
    protected open val doSpecialIcon = false

    protected lateinit var key: String

    init {
        init(attrs, defStyle, defStyleRes)
    }

    protected open fun init(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        orientation = HORIZONTAL

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)
        key = a.getString(R.styleable.SettingView_key)!!

        if (doSpecialIcon) {
            populateIcon(a)
        }
        labelView = TextView(context).apply {
            if (!doSpecialIcon) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(a.getResourceId(R.styleable.SettingView_drawable, 0), 0, 0, 0)
            }
            text = a.getString(R.styleable.SettingView_label)
            textSize = 17f
            includeFontPadding = false
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            compoundDrawablePadding = 15.dp.toInt()
            val h = 8.dp.toInt()
            setPadding(h, 0, h, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                setTextColor(context.getColor(R.color.cardtxt))
            } else {
                setTextColor(context.resources.getColor(R.color.cardtxt))
            }
            layoutParams = LayoutParams(0, 60.dp.toInt(), 1f)
        }
        addView(labelView)
        populate(attrs, defStyle, defStyleRes)
    }

    open fun populateIcon(a: TypedArray) {}
    abstract fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int)
}
