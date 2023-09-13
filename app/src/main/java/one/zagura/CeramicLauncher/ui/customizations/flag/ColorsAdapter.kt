package io.posidon.android.slablauncher.ui.settings.flag

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import one.zagura.CeramicLauncher.util.storage.Settings

class ColorsAdapter : RecyclerView.Adapter<ColorViewHolder>() {
    private var colors = Settings.getStringsOrSetEmpty("flag:colors")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ColorViewHolder(parent, ::onColorChanged) { colors[it].toInt(16) }

    override fun onBindViewHolder(holder: ColorViewHolder, i: Int) {
        holder.bind(colors[i].toInt(16))
    }

    override fun getItemCount() = colors.size

    private fun onColorChanged(context: Context, newColor: Int, i: Int) {
        colors[i] = newColor.toString(16)
        saveColors(context)
    }

    fun addColor(context: Context) {
        colors.add("0")
        notifyItemInserted(colors.lastIndex)
        saveColors(context)
    }

    fun setColors(context: Context, colors: ArrayList<String>) {
        this.colors = colors
        notifyDataSetChanged()
        saveColors(context)
    }

    fun removeColor(context: Context, i: Int) {
        colors.removeAt(i)
        notifyItemRemoved(i)
        saveColors(context)
    }

    fun onMove(
        context: Context,
        from: Int,
        to: Int,
    ) {
        colors.add(to, colors.removeAt(from))
        notifyItemMoved(from, to)
        saveColors(context)
    }

    fun saveColors(context: Context) {
        Settings["flag:colors"] = colors
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateColors() {
        colors = Settings.getStringsOrSetEmpty("flag:colors")
        notifyDataSetChanged()
    }
}