package one.zagura.CeramicLauncher.util.theme

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.luminance
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.posidon.android.conveniencelib.drawable.toBitmap
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.drawable.ColorPreviewDrawable
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object ColorTools {

    fun pastelizeColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = min(hsv[1],0.5f)
        hsv[2] = min(max(0.4f, hsv[2]), 0.75f)
        return Color.HSVToColor(hsv)
    }

    fun makeContrasty(color: Int, against: Int): Int {
        val lab = DoubleArray(3)
        ColorUtils.colorToLAB(color, lab)
        if (against.luminance > 0.5f) lab[0] = lab[0].coerceAtMost(10.0)
        else lab[0] = lab[0].coerceAtLeast(85.0)
        return ColorUtils.LABToColor(lab[0], lab[1], lab[2])
    }

    inline fun colorPreview(@ColorInt color: Int, context: Context): Drawable {
        return ColorPreviewDrawable(color, context)
    }

    inline fun iconBadge(@ColorInt color: Int): Drawable {
        val d = GradientDrawable()
        d.shape = GradientDrawable.OVAL
        d.setColor(color)
        d.setStroke(1, 0x55000000)
        return d
    }

    val randomColors = arrayOf(
        0xffa473ff.toInt(),
        0xff578cff.toInt(),
        0xff04A5AD.toInt(),
        0xff66b45f.toInt(),
        0xfff2a735.toInt(),
        0xfff6724b.toInt(),
        0xffee3264.toInt(),
    )

    fun getColorForText(text: CharSequence): Int =
        randomColors[Random(text.hashCode()).nextInt(randomColors.size)]

    fun pickColor(context: Context, @ColorInt initColor: Int, onSelect: (color: Int) -> Unit) {
        val d = BottomSheetDialog(context, R.style.bottomsheet)
        d.setContentView(R.layout.color_picker)
        d.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.color.ui_card_background)
        val alpha = d.findViewById<SeekBar>(R.id.alpha)!!
        val red = d.findViewById<SeekBar>(R.id.red)!!
        val green = d.findViewById<SeekBar>(R.id.green)!!
        val blue = d.findViewById<SeekBar>(R.id.blue)!!
        val txt = d.findViewById<EditText>(R.id.hextxt)!!
        val okBtn = d.findViewById<TextView>(R.id.ok)!!
        d.findViewById<RecyclerView>(R.id.recycler)!!.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ColorAdapter(getWallpaperColors(context).apply {
                addAll(randomColors)
            }).apply { onItemClickListener = { color -> txt.setText(Integer.toHexString(color)) }}
        }
        var updatingAllowed = true
        txt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = try {
                val color = s.toString().toLong(16).toInt()
                d.findViewById<View>(R.id.bgColorPrev)!!.setBackgroundColor(color)
                if (updatingAllowed) {
                    updatingAllowed = false
                    alpha.progress = color shr 24 and 0xff
                    red.progress = color shr 16 and 0xff
                    green.progress = color shr 8 and 0xff
                    blue.progress = color and 0xff
                    updatingAllowed = true
                }
                val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                txt.setTextColor(txtColor)
                okBtn.setTextColor(txtColor)
                okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
            } catch (ignore: NumberFormatException) {}
        })
        txt.setText(Integer.toHexString(initColor))
        val txtColor = if (initColor.luminance > .6f) -0xdad9d9 else -0x1
        txt.setTextColor(txtColor)
        okBtn.setTextColor(txtColor)
        okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
        okBtn.setOnClickListener {
            var newColor = initColor
            try { newColor = txt.text.toString().toLong(16).toInt() }
            catch (e: NumberFormatException) { Toast.makeText(context, "That's not a color.", Toast.LENGTH_SHORT).show() }
            onSelect(newColor)
            d.dismiss()
        }
        val hex = StringBuilder(txt.text.toString())
        while (hex.length != 8) hex.insert(0, 0)
        alpha.progress = hex.substring(0, 2).toLong(16).toInt()
        red.progress = hex.substring(2, 4).toLong(16).toInt()
        green.progress = hex.substring(4, 6).toLong(16).toInt()
        blue.progress = hex.substring(6, 8).toLong(16).toInt()
        alpha.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = progress * 256 * 256 * 256 + red.progress * 256 * 256 + green.progress * 256 + blue.progress
                    txt.setText(Integer.toHexString(color))
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        red.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = alpha.progress * 256 * 256 * 256 + progress * 256 * 256 + green.progress * 256 + blue.progress
                    txt.setText(Integer.toHexString(color))
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        green.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = alpha.progress * 256 * 256 * 256 + red.progress * 256 * 256 + progress * 256 + blue.progress
                    txt.setText(Integer.toHexString(color))
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        blue.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = alpha.progress * 256 * 256 * 256 + red.progress * 256 * 256 + green.progress * 256 + progress
                    txt.setText(Integer.toHexString(color))
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        d.show()
    }

    fun pickColorNoAlpha(context: Context, @ColorInt initColor: Int, onSelect: (color: Int) -> Unit) {
        val d = BottomSheetDialog(context, R.style.bottomsheet)
        d.setContentView(R.layout.color_picker)
        d.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.color.ui_card_background)
        val red = d.findViewById<SeekBar>(R.id.red)!!
        val green = d.findViewById<SeekBar>(R.id.green)!!
        val blue = d.findViewById<SeekBar>(R.id.blue)!!
        val txt = d.findViewById<EditText>(R.id.hextxt)!!
        val okBtn = d.findViewById<TextView>(R.id.ok)!!
        d.findViewById<RecyclerView>(R.id.recycler)!!.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ColorAdapter(getWallpaperColors(context).apply {
                addAll(randomColors)
            }).apply { onItemClickListener = { color -> txt.setText(Integer.toHexString(color and 0xffffff)) }}
        }
        var updatingAllowed = true
        val hex = StringBuilder(txt.text.toString())
        txt.filters = arrayOf<InputFilter>(LengthFilter(6))
        txt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = try {
                val color = "ff$s".toLong(16).toInt()
                d.findViewById<View>(R.id.bgColorPrev)!!.setBackgroundColor(color)
                if (updatingAllowed) {
                    updatingAllowed = false
                    red.progress = color shr 16 and 0xff
                    green.progress = color shr 8 and 0xff
                    blue.progress = color and 0xff
                    updatingAllowed = true
                }
                val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                txt.setTextColor(txtColor)
                okBtn.setTextColor(txtColor)
                okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
            } catch (ignore: NumberFormatException) {}
        })
        txt.setText(initColor.toString(16).padStart(6, '0'))
        val txtColor = if (initColor.luminance > .6f) -0xdad9d9 else -0x1
        txt.setTextColor(txtColor)
        okBtn.setTextColor(txtColor)
        okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
        okBtn.setOnClickListener {
            var newColor = initColor
            try { newColor = txt.text.toString().toLong(16).toInt() }
            catch (e: NumberFormatException) { Toast.makeText(context, "That's not a color.", Toast.LENGTH_SHORT).show() }
            onSelect(newColor)
            d.dismiss()
        }
        d.findViewById<View>(R.id.alpha)!!.visibility = View.GONE
        while (hex.length != 8) hex.insert(0, 0)
        red.progress = hex.substring(2, 4).toLong(16).toInt()
        green.progress = hex.substring(4, 6).toLong(16).toInt()
        blue.progress = hex.substring(6, 8).toLong(16).toInt()
        red.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = progress * 256 * 256 + green.progress * 256 + blue.progress
                    val hex = StringBuilder(Integer.toHexString(color))
                    while (hex.length != 6) hex.insert(0, 0)
                    txt.setText(hex.toString())
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        green.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = red.progress * 256 * 256 + progress * 256 + blue.progress
                    val hex = StringBuilder(Integer.toHexString(color))
                    while (hex.length != 6) hex.insert(0, 0)
                    txt.setText(hex.toString())
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        blue.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (updatingAllowed) {
                    updatingAllowed = false
                    val color = red.progress * 256 * 256 + green.progress * 256 + progress
                    val hex = StringBuilder(Integer.toHexString(color))
                    while (hex.length != 6) hex.insert(0, 0)
                    txt.setText(hex.toString())
                    val txtColor = if (color.luminance > .6f) -0xdad9d9 else -0x1
                    txt.setTextColor(txtColor)
                    okBtn.setTextColor(txtColor)
                    okBtn.backgroundTintList = ColorStateList.valueOf(0x00ffffff and txtColor or 0x33000000)
                    updatingAllowed = true
                }
            }
        })
        d.show()
    }

    private fun getWallpaperColors(context: Context) = ArrayList<Int>().apply {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val drawable = WallpaperManager.getInstance(context).fastDrawable ?: return@apply
            val palette = Palette.from(drawable.toBitmap()).generate()
            add(palette.getDominantColor(0))
            add(palette.getDarkVibrantColor(0))
            add(palette.getVibrantColor(0))
            add(palette.getLightMutedColor(0))
            add(palette.getMutedColor(0))
            add(palette.getDarkMutedColor(0))
            add(palette.getLightVibrantColor(0))
            removeIf { it == 0 }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val wallpaperColors = WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM) ?: return@apply
            add(wallpaperColors.primaryColor.toArgb())
            wallpaperColors.secondaryColor?.let { add(it.toArgb()) }
            wallpaperColors.tertiaryColor?.let { add(it.toArgb()) }
        }
    }.toMutableSet().toMutableList()

    fun formatColor(color: Int): String {
        var a = color.toUInt().toULong().toString(16).uppercase().padStart(8, '0')
        if (a.startsWith("FF"))
            a = a.substring(2)
        return "#$a"
    }

    class ColorAdapter(
        private val colors: List<Int>
    ) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

        var onItemClickListener: ((color: Int) -> Unit)? = null

        class ColorViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun getItemCount() = colors.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ColorViewHolder(ImageView(Tools.appContext).apply {
                val p = 8.dp.toPixels(parent)
                val size = 36.dp.toPixels(parent) + p * 2
                maxWidth = size
                maxHeight = size
                minimumWidth = size
                minimumHeight = size
                setPadding(p, p, p, p)
            })

        override fun onBindViewHolder(holder: ColorViewHolder, i: Int) {
            holder.imageView.apply {
                setImageDrawable(colorPreview(colors[i], context))
                setOnClickListener {
                    onItemClickListener?.invoke(colors[i])
                }
            }
        }
    }
}