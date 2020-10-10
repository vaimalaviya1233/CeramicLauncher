package posidon.launcher.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import posidon.launcher.R
import posidon.launcher.storage.Settings
import posidon.launcher.tools.dp
import posidon.launcher.view.Switch

class HeaderSwitchSettingView : HeaderSettingView {

    private lateinit var switch: Switch

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet?) : super(c, a)
    constructor(c: Context, a: AttributeSet?, sa: Int) : super(c, a, sa)
    constructor(c: Context, a: AttributeSet?, sa: Int, sr: Int) : super(c, a, sa, sr)

    var onCheckedChange: ((Boolean) -> Unit)? = null

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)

        val key = a.getString(R.styleable.SettingView_key)!!

        val default = a.getBoolean(R.styleable.SettingView_def, false)

        switch = Switch(context).apply {
            val p = 12.dp.toInt()
            setPadding(p, p, p, p)
            layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT, Gravity.CENTER_VERTICAL or Gravity.END).apply {
                marginEnd = 12.dp.toInt()
            }
            isChecked = Settings[key, default]
            setOnCheckedChangeListener { _, checked ->
                Settings[key] = checked
                onCheckedChange?.invoke(checked)
            }
        }
        addView(switch)

        a.recycle()
    }
}