package one.zagura.CeramicLauncher.util.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

internal class ContactDrawable(
    color: Int,
    val character: Char,
    val paint: Paint,
    val overlayPaint: Paint,
) : Drawable() {

    val bgPaint = Paint().apply {
        this.color = color
    }

    override fun draw(canvas: Canvas) {
        val w = bounds.width() / 2f
        canvas.drawCircle(w, w, w, bgPaint)
        val x = bounds.width() / 2f
        val y = (bounds.height() - (paint.descent() + paint.ascent())) / 2f
        canvas.drawText(charArrayOf(character), 0, 1, x, y, paint)
        canvas.drawText(charArrayOf(character), 0, 1, x, y, overlayPaint)
    }

    override fun getOpacity() = PixelFormat.OPAQUE

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}

    override fun getIntrinsicWidth() = 128
    override fun getIntrinsicHeight() = 128
    override fun getMinimumWidth() = 128
    override fun getMinimumHeight() = 128
}