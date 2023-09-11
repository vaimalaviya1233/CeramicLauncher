package one.zagura.CeramicLauncher.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.StaticShortcut

class ShortcutAdapter(
    private val context: Context,
    private val shortcuts: List<StaticShortcut>,
) : RecyclerView.Adapter<ShortcutAdapter.ShortcutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.shortcut, parent, false)
        return ShortcutViewHolder(v).apply {
            view.setOnClickListener {
                ItemLongPress.currentPopup?.dismiss()
                shortcuts[adapterPosition].open(context, view, -1)
            }
        }
    }

    class ShortcutViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: ShortcutViewHolder, position: Int) {
        val shortcut = shortcuts[position]
        val txt = holder.view.findViewById<TextView>(R.id.icontxt)
        txt.text = shortcut.label
        txt.setTextColor(holder.view.context.getColor(R.color.ui_card_text))
        holder.view.findViewById<ImageView>(R.id.iconimg).setImageDrawable(shortcut.icon)
    }

    override fun getItemCount(): Int = shortcuts.size

}