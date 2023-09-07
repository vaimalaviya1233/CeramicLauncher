package one.zagura.CeramicLauncher.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.Switch

class SwitchSettingView : SettingView {

    private lateinit var switch: Switch

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : super(c, a)
    constructor(c: Context, a: AttributeSet, sa: Int) : super(c, a, sa)
    constructor(c: Context, a: AttributeSet, sa: Int, sr: Int) : super(c, a, sa, sr)

    var default: Boolean = false

    constructor(c: Context, key: String, default: Boolean, labelId: Int, iconId: Int) : super(c, key, labelId, iconId) {
        this.default = default
        this.value = Settings[key, default]
    }

    var value: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    var onCheckedChange: ((Boolean) -> Unit)? = null

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)
        default = a.getBoolean(R.styleable.SettingView_def, false)

        switch = Switch(context).apply {
            val p = 12.dp.toPixels(context)
            setPadding(p, p, p, p)
            layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
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