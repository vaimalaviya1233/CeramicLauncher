package one.zagura.CeramicLauncher.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.data.items.LauncherItem
import one.zagura.CeramicLauncher.ui.ItemLongPress
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.theme.Icons

internal class SearchAdapter(
    private val context: Context,
    private val results: List<LauncherItem>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun getItemCount(): Int = results.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(
            view,
            view.findViewById(R.id.iconimg),
            view.findViewById(R.id.icontxt),
            view.findViewById(R.id.notificationBadge)).apply {
            view.setOnClickListener {
                results[adapterPosition].open(it.context, it, -1)
            }
            view.setOnLongClickListener {
                val app = results[adapterPosition]
                if (app is App)
                    ItemLongPress.onItemLongPress(it.context, it, app, null, null)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = holder.icon
        val text = holder.text
        val item = results[position]
        icon.setImageDrawable(item.icon)
        text.text = item.label
        text.setTextColor(Settings["search:ui:text_color", -0x1])
        if (item is App && Settings["notif:badges", true] && item.notificationCount != 0) {
            val badge = holder.notificationBadge
            badge.visibility = View.VISIBLE
            badge.text = if (Settings["notif:badges:show_num", true]) item.notificationCount.toString() else ""
            Icons.generateNotificationBadgeBGnFG(item.icon!!) { bg, fg ->
                badge.background = bg
                badge.setTextColor(fg)
            }
        } else {
            holder.notificationBadge.visibility = View.GONE
        }
        val appSize = Settings["search:icons:size", 56].dp.toPixels(context)
        holder.icon.layoutParams.height = appSize
        holder.icon.layoutParams.width = appSize
    }

    override fun getItemId(position: Int): Long = 0

    class ViewHolder(
        view: View,
        var icon: ImageView,
        var text: TextView,
        var notificationBadge: TextView) : RecyclerView.ViewHolder(view)
}