package posidon.launcher.view

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import posidon.launcher.R
import posidon.launcher.tools.Device
import posidon.launcher.tools.Tools
import posidon.launcher.tools.dp
import posidon.launcher.tools.sp
import kotlin.math.max
import kotlin.math.min

class Spinner : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    var data: Array<String> = emptyArray()
    var onSelectionChangedListener: ((Spinner) -> Unit)? = null

    inline fun setSelectionChangedListener(
        noinline listener: ((Spinner) -> Unit)?
    ) { onSelectionChangedListener = listener }

    init {
        setOnClickListener {
            var popup: PopupWindow? = null
            var height = 0f
            popup = PopupWindow(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(8.dp.toInt(), 8.dp.toInt(), 8.dp.toInt(), 8.dp.toInt())
                for (i in data.indices) {
                    addView(TextView(context).apply {
                        text = data[i]
                        setOnClickListener {
                            selectionI = i
                            popup!!.dismiss()
                            onSelectionChangedListener?.invoke(this@Spinner)
                        }
                        textSize = 18f
                        setTextColor(0xffffffff.toInt())
                        setPadding(18.dp.toInt(), 9.dp.toInt(), 18.dp.toInt(), 9.dp.toInt())
                        includeFontPadding = false
                        height += 18f.sp + 18f.dp
                    })
                }
            }, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true).apply {
                val bg = ShapeDrawable()
                val r = 18.dp
                bg.shape = RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null)
                bg.paint.color = context.resources.getColor(R.color.cardbg)
                setBackgroundDrawable(bg)
            }
            val location = IntArray(2)
            getLocationInWindow(location)
            popup.showAtLocation(this, Gravity.TOP, location[0], min(location[1], Device.displayHeight - Tools.navbarHeight - height.toInt()))
        }
    }

    val selection: String get() = data[selectionI]

    var selectionI: Int = 0
        set(value) {
            field = value
            text = selection
        }
}