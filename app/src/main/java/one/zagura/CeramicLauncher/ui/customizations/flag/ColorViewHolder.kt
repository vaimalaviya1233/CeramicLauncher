package io.posidon.android.slablauncher.ui.settings.flag

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.luminance
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.theme.ColorTools

class ColorViewHolder(
    parent: ViewGroup,
    onColorChanged: (Context, newColor: Int, i: Int) -> Unit,
    getColor: (i: Int) -> Int,
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.flag_color_stripe, parent, false)) {

    init {
        itemView.setOnClickListener {
            ColorTools.pickColor(it.context, getColor(adapterPosition)) { color ->
                onColorChanged(itemView.context, color, adapterPosition)
                it.setBackgroundColor(color)
                with(it.findViewById<TextView>(R.id.text)) {
                    text = ColorTools.formatColor(color)
                    setTextColor((if (color.luminance > .6f) 0xff000000 else 0xffffffff).toInt())
                }
            }
        }
    }

    fun bind(color: Int) {
        itemView.setBackgroundColor(color)
        with(itemView.findViewById<TextView>(R.id.text)) {
            text = ColorTools.formatColor(color)
            setTextColor((if (color.luminance > .6f) 0xff000000 else 0xffffffff).toInt())
        }
    }
}