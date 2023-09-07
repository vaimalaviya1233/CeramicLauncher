package one.zagura.CeramicLauncher.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.StateSet
import androidx.appcompat.widget.SwitchCompat
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.tools.vibrate

class Switch(
    context: Context,
    attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {

    var accentColor = Global.accentColor
        set(value) {
            field = value
            trackDrawable = generateTrackDrawable()
            thumbDrawable = generateThumbDrawable()
            refreshDrawableState()
        }

    init {
        trackDrawable = generateTrackDrawable()
        thumbDrawable = generateThumbDrawable()
    }

    override fun performClick(): Boolean {
        context.vibrate()
        return super.performClick()
    }

    private fun generateTrackDrawable(): Drawable {
        val out = StateListDrawable()
        out.addState(intArrayOf(android.R.attr.state_checked), generateBG(context, accentColor and 0x00ffffff or 0x55000000))
        out.addState(StateSet.WILD_CARD, generateBG(context, 0x0effffff))
        return out
    }

    private fun generateThumbDrawable(): Drawable {
        val out = StateListDrawable()
        out.addState(intArrayOf(android.R.attr.state_checked), generateCircle(context, accentColor))
        out.addState(StateSet.WILD_CARD, generateCircle(context, 0x2affffff))
        return out
    }

    companion object {

        fun generateCircle(context: Context, color: Int): Drawable {
            val r = 14.dp.toPixels(context)
            val inset = 5.dp.toPixels(context)
            return LayerDrawable(arrayOf(
                GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(context.getColor(R.color.ui_card_background))
                    setSize(r, r)
                },
                GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setSize(r, r)
                },
            )).apply {
                setLayerInset(0, inset, inset, inset, inset)
                val inset = inset + 4.dp.toPixels(context)
                setLayerInset(1, inset, inset, inset, inset)
            }
        }

        fun generateBG(context: Context, color: Int): Drawable {
            return GradientDrawable().apply {
                cornerRadius = Float.MAX_VALUE
                setColor(color)
            }
        }
    }
}
