package one.zagura.CeramicLauncher.util.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.Tools

internal class ContactDrawable(
    color: Int,
    val text: String,
    val paint: Paint,
) : Drawable() {

    val bgPaint = Paint().apply {
        this.color = color
        isAntiAlias = true
    }

    override fun draw(canvas: Canvas) {
        val w = bounds.width() / 2f
        canvas.drawCircle(w, w, w - 1f, bgPaint)
        val tmp = bgPaint.color
        bgPaint.color = Tools.appContext!!.getColor(R.color.ui_card_background)
        canvas.drawCircle(w, w, w - 1f - 4.dp.toFloatPixels(Tools.appContext!!), bgPaint)
        bgPaint.color = tmp
        val x = bounds.width() / 2f
        val y = (bounds.height() - (paint.descent() + paint.ascent())) / 2f
        canvas.drawText(text, x, y, paint)
    }

    override fun getOpacity() = PixelFormat.OPAQUE

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}

    override fun getIntrinsicWidth() = 128
    override fun getIntrinsicHeight() = 128
    override fun getMinimumWidth() = 128
    override fun getMinimumHeight() = 128
}