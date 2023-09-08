package one.zagura.CeramicLauncher.items.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.tools.Tools
import one.zagura.CeramicLauncher.tools.theme.Customizer
import one.zagura.CeramicLauncher.tools.theme.Icons
import one.zagura.CeramicLauncher.view.recycler.HighlightAdapter

class DrawerAdapter : BaseAdapter(), SectionIndexer, HighlightAdapter {

    override fun getCount(): Int = Global.apps.size
    override fun getItem(i: Int) = Global.apps[i]
    override fun getItemId(i: Int): Long = 0

    internal class ViewHolder(
        val icon: ImageView,
        val text: TextView,
        val notificationBadge: TextView)

    override fun getView(i: Int, cv: View?, parent: ViewGroup): View {
        var convertView = cv
        val holder: ViewHolder
        val app = Global.apps[i]
        if (convertView == null) {
            val li = Tools.appContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = if (Settings["drawer:columns", 5] > 2) li.inflate(R.layout.drawer_item, parent, false)
                else li.inflate(R.layout.list_item, parent, false)
            holder = ViewHolder(
                convertView.findViewById(R.id.iconimg),
                convertView.findViewById(R.id.icontxt),
                convertView.findViewById(R.id.notificationBadge))
            val appSize = Settings["drawer:icons:size", 64].dp.toPixels(parent.context)
            holder.icon.layoutParams.height = appSize
            holder.icon.layoutParams.width = appSize
            convertView.tag = holder

            if (Settings["drawer:labels:enabled", true]) {
                holder.text.text = app.label
                holder.text.visibility = View.VISIBLE
                Customizer.styleLabel("drawer:labels", holder.text, 0xddffffff.toInt(), 12)
            } else holder.text.visibility = View.INVISIBLE

        } else {
            holder = convertView.tag as ViewHolder
            holder.text.text = app.label
        }
        convertView!!.background = if (highlightI == i) HighlightAdapter.createHighlightDrawable(convertView.resources) else null
        holder.icon.setImageDrawable(app.icon)
        if (Settings["notif:badges", true] && app.notificationCount != 0) {
            val badge = holder.notificationBadge
            badge.visibility = View.VISIBLE
            badge.text = if (Settings["notif:badges:show_num", true]) app.notificationCount.toString() else ""
            Icons.generateNotificationBadgeBGnFG(app.icon!!) { bg, fg ->
                badge.background = bg
                badge.setTextColor(fg)
            }
        } else {
            holder.notificationBadge.visibility = View.GONE
        }
        return convertView
    }

    private val savedSections = Array(Global.appSections.size) { Global.appSections[it][0].label[0].uppercaseChar() }

    override fun getSections(): Array<Char> = savedSections

    override fun getSectionForPosition(i: Int): Int {
        val label = Global.apps[i].label
        return savedSections.indexOf(if (label.isEmpty()) ' ' else label[0].uppercaseChar())
    }

    override fun getPositionForSection(i: Int): Int {
        val char = savedSections[i]
        for (j in Global.apps.indices) {
            val label = Global.apps[j].label
            if (char == if (label.isEmpty()) ' ' else label[0].uppercaseChar()) {
                return j
            }
        }
        return 0
    }

    private var highlightI = -1

    override fun highlight(i: Int) {
        highlightI = i
    }

    override fun unhighlight() {
        highlightI = -1
    }
}