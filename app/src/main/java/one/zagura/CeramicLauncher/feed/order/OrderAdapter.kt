package one.zagura.CeramicLauncher.feed.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.items.App
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.SwipeableLayout

class OrderAdapter(
    val context: Context,
    val sections: ArrayList<String>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(
        val swipeView: SwipeableLayout,
        val icon: ImageView,
        val text: TextView
    ) : RecyclerView.ViewHolder(swipeView)

    override fun getItemCount() = sections.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val icon = ImageView(context).apply {
            run {
                val p = 30.dp.toPixels(context)
                setPaddingRelative(p, p, 10.dp.toPixels(context), p)
            }
        }
        val text = TextView(context).apply {
            setTextColor(0xffffffff.toInt())
            textSize = 15f
        }
        val linearLayout = LinearLayout(context).apply {
            gravity = Gravity.CENTER_VERTICAL

            addView(icon, let {
                val s = 108.dp.toPixels(context)
                ViewGroup.LayoutParams(s, s)
            })
            addView(text)
            setOnClickListener {}
        }
        val swipeView = SwipeableLayout(linearLayout).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
            setCardBackgroundColor(context.resources.getColor(R.color.ui_card_background))
            setIconColor(Global.accentColor)
            setSwipeColor(Global.accentColor and 0x11ffffff)
            useCompatPadding = true
            cardElevation = 10f
            radius = 12.dp.toFloatPixels(context)
        }
        return ViewHolder(swipeView, icon, text)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val section = sections[i]
        holder.swipeView.reset()
        holder.swipeView.onSwipeAway = {
            sections.remove(section)
            notifyItemRemoved(i)
            notifyItemRangeChanged(i, sections.size - i)
        }
        when (section) {
            "starred_contacts" -> {
                holder.text.text = context.getString(R.string.starred_contacts)
                holder.icon.setImageResource(R.drawable.ic_apps)
                holder.icon.imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
            }
            "notifications" -> {
                holder.text.text = context.getString(R.string.notifications)
                holder.icon.setImageResource(R.drawable.ic_notification)
                holder.icon.imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
            }
            "news" -> {
                holder.text.text = context.getString(R.string.settings_title_news)
                holder.icon.setImageResource(R.drawable.ic_news)
                holder.icon.imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
            }
            "music" -> {
                holder.text.text = context.getString(R.string.media_player)
                holder.icon.setImageResource(R.drawable.ic_play)
                holder.icon.imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
            }
            else -> {
                val prefix = section.substringBefore(':')
                val value = section.substringAfter(':')
                when (prefix) {
                    "widget" -> {
                        val component = Settings.getString("widget:$value") ?: return
                        val packageName = component.substringBefore('/')
                        try {
                            holder.text.text = "Widget" + " | " + (App.getFromPackage(packageName)?.get(0)?.label ?: context.packageManager.getApplicationInfo(packageName, 0).loadLabel(context.packageManager))
                            holder.icon.setImageDrawable(App.getFromPackage(packageName)?.get(0)?.icon ?: context.packageManager.getApplicationIcon(packageName))
                            holder.icon.imageTintList = null
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println(component)
                        }
                    }
                    "spacer" -> {
                        holder.text.text = context.getString(R.string.spacer)
                        holder.icon.setImageResource(R.drawable.ic_apps)
                        holder.icon.imageTintList = ColorStateList.valueOf(0xffffffff.toInt())
                    }
                    else -> {
                        sections.remove(section)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}