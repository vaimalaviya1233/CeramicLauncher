package one.zagura.CeramicLauncher.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.Spinner

class SpinnerSettingView : IntSettingView {

    private lateinit var spinner: Spinner

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : this(c, a, 0)
    constructor(c: Context, a: AttributeSet, sa: Int) : this(c, a, sa, 0)
    constructor(c: Context, attrs: AttributeSet, sa: Int, sr: Int) : super(c, attrs, sa, sr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SpinnerSettingView, sa, sr)
        array = a.getTextArray(R.styleable.SpinnerSettingView_array)
        selectionI = Settings[key, default]
        a.recycle()
    }

    constructor(c: Context, key: String, default: Int, labelId: Int, iconId: Int) : super(c, key, default, labelId, iconId)

    var array: Array<out CharSequence>
        get() = spinner.data
        set(value) {
            spinner.data = value
        }
    var selectionI
        get() = spinner.selectionI
        set(value) {
            spinner.selectionI = value
        }

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        spinner = Spinner(context).apply {

            includeFontPadding = false
            textSize = 15f
            setTextColor(context.resources.getColor(R.color.cardspinnertxt))
            gravity = Gravity.START or Gravity.CENTER_VERTICAL

            val h = 8.dp.toPixels(context)
            setPadding(h, 0, h, 0)

            setSelectionChangedListener {
                Settings[key] = it.selectionI
            }
        }
        addView(spinner, LayoutParams(WRAP_CONTENT, 60.dp.toPixels(context)))
    }
}