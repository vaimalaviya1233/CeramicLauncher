package one.zagura.CeramicLauncher.ui.view

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatSeekBar
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R

class Seekbar(context: Context, attrs: AttributeSet? = null) : AppCompatSeekBar(context, attrs) {

    init {
        progressDrawable = generateDrawable()
        thumb = generateThumb()
        splitTrack = false
    }

    private fun generateDrawable(): Drawable {
        val out = LayerDrawable(arrayOf(
            Switch.generateBG(context, context.getColor(R.color.ui_background)),
            ClipDrawable(Switch.generateBG(context, context.getColor(R.color.ui_card_text)), Gravity.LEFT, GradientDrawable.Orientation.BL_TR.ordinal)
        ))
        val inset = 7.dp.toPixels(context)
        out.setLayerInset(0, inset, inset, inset, inset)
        out.setLayerInset(1, inset, inset, inset, inset)
        out.setId(0, android.R.id.background)
        out.setId(1, android.R.id.progress)
        return out
    }

    private fun generateThumb(): Drawable {
        return Switch.generateCircle(context, Global.accentColor)
    }
}