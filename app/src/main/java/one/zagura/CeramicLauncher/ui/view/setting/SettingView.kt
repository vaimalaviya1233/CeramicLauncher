package one.zagura.CeramicLauncher.ui.view.setting

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R

abstract class SettingView : LinearLayout {

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
    ) : super(context, attrs, defStyle, defStyleRes) {
        init(attrs, defStyle, defStyleRes)
    }

    constructor(context: Context, key: String, labelId: Int, subtitleID: Int, iconId: Int) : super(context) {
        this.key = key
        if (doSpecialIcon)
            populateIcon()
        else createIcon(iconId)
        createLabelView(resources.getString(labelId), if (subtitleID == 0) null else resources.getString(subtitleID))
        populate(null, 0, 0)
    }

    lateinit var key: String

    protected lateinit var labelView: TextView
    protected lateinit var subtitleView: TextView
    protected lateinit var iconView: ImageView

    protected open val doSpecialIcon = false

    protected open fun init(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {

        orientation = HORIZONTAL
        gravity = Gravity.START or Gravity.CENTER_VERTICAL

        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyle, defStyleRes)
        a.getString(R.styleable.SettingView_key)?.let { key = it }

        if (doSpecialIcon)
            populateIcon()
        else createIcon(a.getResourceId(R.styleable.SettingView_drawable, 0))
        createLabelView(a.getString(R.styleable.SettingView_label), a.getString(R.styleable.SettingView_subtitle))
        populate(attrs, defStyle, defStyleRes)
    }

    private fun createIcon(iconId: Int) {
        iconView = ImageView(context).apply {
            setImageResource(iconId)
            imageTintList = ColorStateList.valueOf(Global.getPastelAccent())
            val h = 8.dp.toPixels(context)
            setPadding(h, h, h, h)
        }
        addView(iconView, LayoutParams(48.dp.toPixels(context), LayoutParams.MATCH_PARENT))
    }

    private fun createLabelView(label: String?, subtitle: String?) {
        labelView = TextView(context).apply {
            text = label
            textSize = 16f
            includeFontPadding = false
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            val h = 8.dp.toPixels(context)
            setPadding(h, 0, h, 0)
            setTextColor(context.getColor(R.color.ui_card_text))
        }
        if (subtitle == null)
            addView(labelView, LayoutParams(0, 60.dp.toPixels(context), 1f))
        else {
            val ll = LinearLayout(context).apply {
                orientation = VERTICAL
                gravity = Gravity.START or Gravity.CENTER_VERTICAL

                subtitleView = TextView(context).apply {
                    text = subtitle
                    textSize = 12f
                    includeFontPadding = false
                    val h = 8.dp.toPixels(context)
                    setPadding(h, 0, h, 0)
                    setTextColor(context.getColor(R.color.ui_card_text_secondary))
                }

                addView(labelView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
                addView(subtitleView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            }
            addView(ll, LayoutParams(0, 60.dp.toPixels(context), 1f))
        }
    }

    open fun populateIcon() {}
    abstract fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int)

    var label: CharSequence
        get() = labelView.text
        set(value) {
            labelView.text = value
        }
}
