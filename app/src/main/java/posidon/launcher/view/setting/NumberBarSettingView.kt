package posidon.launcher.view.setting

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.storage.Settings
import posidon.launcher.tools.dp
import posidon.launcher.view.Seekbar

class NumberBarSettingView : IntSettingView {

    private lateinit var seekBar: Seekbar
    private lateinit var textIcon: TextView

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : super(c, a)
    constructor(c: Context, a: AttributeSet, sa: Int) : super(c, a, sa)
    constructor(c: Context, a: AttributeSet, sa: Int, sr: Int) : super(c, a, sa, sr)

    override val doSpecialIcon get() = true
    override fun populateIcon(a: TypedArray) {
        textIcon = TextView(context).apply {
            layoutParams = LayoutParams(48.dp.toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            textSize = 28f
            setTextColor(context.resources.getColor(R.color.cardtxticon))
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = resources.getFont(R.font.posidon_sans)
            }
        }
        addView(textIcon)
    }

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.NumberBarSettingView, defStyle, defStyleRes)

        labelView.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 60.dp.toInt())

        val startsWith1 = a.getBoolean(R.styleable.NumberBarSettingView_startsWith1, false)
        val isFloat = a.getBoolean(R.styleable.NumberBarSettingView_isFloat, false)

        seekBar = Seekbar(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER_VERTICAL
            }
            run {
                var m = a.getInt(R.styleable.NumberBarSettingView_max, 0)
                if (startsWith1) m--
                max = m
            }
            run {
                var p = if (isFloat) Settings[key, default.toFloat()].toInt() else Settings[key, default]
                textIcon.text = p.toString()
                if (startsWith1) p--
                progress = p
            }
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(s: SeekBar) {}
                override fun onStopTrackingTouch(s: SeekBar) {}
                override fun onProgressChanged(s: SeekBar, progress: Int, isUser: Boolean) {
                    var p = progress
                    if (startsWith1) p++
                    if (isFloat) Settings[key] = p.toFloat() else Settings[key] = p
                    textIcon.text = p.toString()
                    Main.customized = true
                }
            })
        }
        addView(seekBar)
        a.recycle()
    }
}