package one.zagura.CeramicLauncher.ui.drawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import one.zagura.CeramicLauncher.R

class ColorPreviewDrawable(
    val color: Int,
    context: Context,
) : Drawable() {

    val strokeColor = context.getColor(R.color.ui_background)
    val strokeWidth = 1.dp.toFloatPixels(context)

    private val strokePaint = Paint().apply {
        this.isAntiAlias = true
        this.color = strokeColor
    }
    private val alphaBGPaint = Paint().apply {
        this.isAntiAlias = true
        this.shader = BitmapShader(context.getDrawable(R.drawable.alpha_bg)!!.toBitmap(320, 320), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }
    private val paint = Paint().apply {
        this.isAntiAlias = true
        this.color = this@ColorPreviewDrawable.color
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            bounds.width() / 4f,
            bounds.height() / 4f,
            strokePaint,
        )
        canvas.drawRoundRect(
            bounds.left.toFloat() + strokeWidth,
            bounds.top.toFloat() + strokeWidth,
            bounds.right.toFloat() - strokeWidth,
            bounds.bottom.toFloat() - strokeWidth,
            (bounds.width() - strokeWidth * 2f) / 4f,
            (bounds.height() - strokeWidth * 2f) / 4f,
            alphaBGPaint,
        )
        canvas.drawRoundRect(
            bounds.left.toFloat() + strokeWidth,
            bounds.top.toFloat() + strokeWidth,
            bounds.right.toFloat() - strokeWidth,
            bounds.bottom.toFloat() - strokeWidth,
            (bounds.width() - strokeWidth * 2f) / 4f,
            (bounds.height() - strokeWidth * 2f) / 4f,
            paint,
        )
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}