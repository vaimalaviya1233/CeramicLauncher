package one.zagura.CeramicLauncher.ui.view.setting

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import android.widget.TextView
import io.posidon.android.conveniencelib.Device
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.sp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.Spinner
import one.zagura.CeramicLauncher.util.Tools
import kotlin.math.min

class SpinnerSettingView : IntSettingView {

    private lateinit var spinner: Spinner

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet) : this(c, a, 0)
    constructor(c: Context, a: AttributeSet, sa: Int) : this(c, a, sa, 0)
    constructor(c: Context, attrs: AttributeSet, sa: Int, sr: Int) : super(c, attrs, sa, sr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SpinnerSettingView, sa, sr)
        data = a.getTextArray(R.styleable.SpinnerSettingView_array)
        selectionI = Settings[key, default]
        a.recycle()
    }

    constructor(c: Context, key: String, default: Int, labelId: Int, iconId: Int) : super(c, key, default, labelId, R.string.color, iconId)

    init { setOnClickListener { choose() } }

    var data: Array<out CharSequence> = emptyArray()
    val selection: CharSequence get() = data[selectionI]

    var selectionI: Int = 0
        set(value) {
            field = value
            subtitleView.text = selection
        }

    private fun choose() {
        var popup: PopupWindow? = null
        var height = 0f
        popup = PopupWindow(LinearLayout(context).apply {
            orientation = VERTICAL
            val p = 8.dp.toPixels(context)
            setPadding(p, p, p, p)
            for (i in data.indices) {
                addView(TextView(context).apply {
                    text = data[i]
                    setOnClickListener {
                        selectionI = i
                        popup!!.dismiss()
                        Settings[key] = selectionI
                    }
                    textSize = 18f
                    setTextColor(0xffffffff.toInt())
                    val vp = 9.dp.toPixels(context)
                    val hp = 18.dp.toPixels(context)
                    setPadding(hp, vp, hp, vp)
                    includeFontPadding = false
                    height += 18.sp.toFloatPixels(context) + 18.dp.toFloatPixels(context)
                })
            }
        }, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true).apply {
            val bg = ShapeDrawable()
            val r = 18.dp.toFloatPixels(context)
            bg.shape = RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null)
            bg.paint.color = context.resources.getColor(R.color.ui_card_background)
            setBackgroundDrawable(bg)
        }
        val location = IntArray(2)
        getLocationInWindow(location)
        popup.showAtLocation(this, Gravity.TOP, location[0], min(location[1], Device.screenHeight(context) - Tools.navbarHeight - height.toInt()))
    }

    override fun populate(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {}
}