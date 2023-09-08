package one.zagura.CeramicLauncher.view.feed.notifications

import android.app.RemoteInput
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.luminance
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.feed.notifications.NotificationItem
import one.zagura.CeramicLauncher.feed.notifications.NotificationService
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.tools.Gestures
import one.zagura.CeramicLauncher.tools.theme.ColorTools
import one.zagura.CeramicLauncher.view.SwipeableLayout
import one.zagura.CeramicLauncher.view.feed.notifications.viewHolders.NotificationViewHolder

class NotificationAdapter : RecyclerView.Adapter<NotificationViewHolder>() {

    private var groups = ArrayList<ArrayList<NotificationItem>>()

    override fun getItemCount() = groups.size

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): NotificationViewHolder {
        val context = parent.context
        val view = RelativeLayout(context)
        val hMargin = Settings["feed:card_margin_x", 16].dp.toPixels(parent)
        val vMargin = Settings["feed:card_margin_y", 9].dp.toPixels(parent)
        view.setPadding(hMargin, vMargin, hMargin, vMargin)

        val ll = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val card = SwipeableLayout(ll).apply {
            val bg = Settings["notif:card_swipe_bg_color", 0x880d0e0f.toInt()]
            setIconColor(if (bg.luminance > .6f) 0xff000000.toInt() else 0xffffffff.toInt())
            setSwipeColor(bg)
            preventCornerOverlap = true
            elevation = 0f
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radius = Settings["feed:card_radius", 8].dp.toFloatPixels(context)
        }
        view.addView(card)

        return NotificationViewHolder(view, card, ll)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, i: Int) {
        val context = holder.view.context
        val group = groups[i]
        holder.linearLayout.removeAllViews()
        for (notificationI in group.indices) {
            val notification = group[notificationI]
            val retView: View
            val view: View = if (notification.isSummary) {
                holder.card.onSwipeAway = {
                    Home.instance.runOnUiThread {
                        val iter = group.iterator()
                        for (n in iter) n.cancel()
                    }
                }
                retView = LayoutInflater.from(context).inflate(R.layout.notification_normal_summary, null)
                retView.apply {
                    setBackgroundColor(Settings["notif:background_color", -0x1])
                    findViewById<View>(R.id.summary_separator).setBackgroundColor(Settings["notif:text_color", -0xdad9d9] and 0xffffff or 0x33000000.toInt())
                }
            } else {
                val v1 = when {
                    notification.max != -1 && notification.progress != -1 && notification.max != notification.progress -> {
                        LayoutInflater.from(context).inflate(R.layout.notification_progress, null).apply {
                            val progressBar = findViewById<ProgressBar>(R.id.progress)
                            if (notification.progress == -2) {
                                progressBar.isIndeterminate = true
                            } else {
                                progressBar.isIndeterminate = false
                                progressBar.progressTintList = ColorStateList.valueOf(Global.accentColor)
                                progressBar.progressBackgroundTintList = ColorStateList.valueOf(Global.accentColor)
                                progressBar.progress = notification.progress
                                progressBar.max = notification.max
                            }
                        }
                    }
                    else -> {
                        LayoutInflater.from(context).inflate(R.layout.notification_normal, null)
                    }
                }.apply {
                    setBackgroundColor(Settings["notif:background_color", -0x1])
                    findViewById<TextView>(R.id.txt).maxLines = Settings["notif:text:max_lines", 3]
                }
                retView = SwipeableLayout(v1) {
                    try {
                        group.remove(notification)
                        if (group.size == 1 && group[0].isSummary) {
                            group[0].cancel()
                        }
                        notification.cancel()
                    }
                    catch (e: Exception) { e.printStackTrace() }
                    NotificationService.update()
                }.apply {
                    val bg = Settings["notif:card_swipe_bg_color", 0x880d0e0f.toInt()]
                    setIconColor(if (bg.luminance > .6f) 0xff000000.toInt() else 0xffffffff.toInt())
                    setSwipeColor(bg)
                }
                if (notification.actions != null && Settings["notif:actions:enabled", false]) {
                    v1.findViewById<LinearLayout>(R.id.action_list).visibility = View.VISIBLE
                    v1.findViewById<View>(R.id.top_separator).visibility = View.VISIBLE
                    v1.findViewById<View>(R.id.top_separator).setBackgroundColor(Settings["notif:text_color", -0xdad9d9] and 0xffffff or 0x33000000.toInt())
                    v1.findViewById<View>(R.id.action_area).setBackgroundColor(Settings["notif:actions:background_color", 0x88e0e0e0.toInt()])
                    for (action in notification.actions) {
                        val a = TextView(context)
                        a.text = action.title
                        a.textSize = 14f
                        a.isAllCaps = true
                        a.typeface = Typeface.DEFAULT_BOLD
                        a.setTextColor(Settings["notif:actions:text_color", -0xdad9d9])
                        val vPadding = 10.dp.toPixels(context)
                        val hPadding = 12.dp.toPixels(context)
                        a.setPadding(hPadding, vPadding, hPadding, vPadding)
                        v1.findViewById<LinearLayout>(R.id.action_list).addView(a)
                        a.setOnClickListener {
                            try {
                                val oldInputs = action.remoteInputs
                                if (oldInputs != null) {
                                    v1.findViewById<View>(R.id.bottom_separator).visibility = View.VISIBLE
                                    v1.findViewById<View>(R.id.bottom_separator).setBackgroundColor(Settings["notif:text_color", -0xdad9d9] and 0xffffff or 0x33000000.toInt())
                                    v1.findViewById<View>(R.id.reply).apply lin@ {
                                        visibility = View.VISIBLE
                                        val imm = getSystemService(context, InputMethodManager::class.java)!!
                                        val textArea = findViewById<EditText>(R.id.replyText).apply {
                                            setTextColor(Settings["notif:title_color", -0xeeeded])
                                            setHintTextColor(Settings["notif:text_color", -0xdad9d9])
                                            requestFocus()
                                            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                                            setOnFocusChangeListener { _, hasFocus ->
                                                if (!hasFocus) {
                                                    text.clear()
                                                    this@lin.visibility = View.GONE
                                                    v1.findViewById<View>(R.id.bottom_separator).visibility = View.GONE
                                                    imm.hideSoftInputFromWindow(windowToken, 0)
                                                }
                                            }
                                        }
                                        findViewById<ImageView>(R.id.cancel).apply {
                                            imageTintList = ColorStateList.valueOf(Settings["notif:title_color", -0xeeeded])
                                            backgroundTintList = ColorStateList.valueOf(Settings["notif:title_color", -0xeeeded] and 0xffffff or 0x33ffffff)
                                            setOnClickListener {
                                                textArea.text.clear()
                                                this@lin.visibility = View.GONE
                                                v1.findViewById<View>(R.id.bottom_separator).visibility = View.GONE
                                            }
                                        }
                                        findViewById<ImageView>(R.id.replySend).apply {
                                            imageTintList = ColorStateList.valueOf(Settings["notif:title_color", -0xeeeded])
                                            backgroundTintList = ColorStateList.valueOf(Settings["notif:title_color", -0xeeeded] and 0xffffff or 0x33ffffff)
                                            setOnClickListener {
                                                val intent = Intent()
                                                val bundle = Bundle()
                                                val actualInputs: ArrayList<RemoteInput> = ArrayList()
                                                for (input in oldInputs) {
                                                    bundle.putCharSequence(input.resultKey, textArea.text)
                                                    val builder = RemoteInput.Builder(input.resultKey)
                                                    builder.setLabel(input.label)
                                                    builder.setChoices(input.choices)
                                                    builder.setAllowFreeFormInput(input.allowFreeFormInput)
                                                    builder.addExtras(input.extras)
                                                    actualInputs.add(builder.build())
                                                }
                                                val inputs = actualInputs.toArray(arrayOfNulls<RemoteInput>(actualInputs.size))
                                                RemoteInput.addResultsToIntent(inputs, intent, bundle)
                                                action.actionIntent.send(context, 0, intent)
                                                textArea.text.clear()
                                                this@lin.visibility = View.GONE
                                                v1.findViewById<View>(R.id.bottom_separator).visibility = View.GONE
                                            }
                                        }
                                    }
                                } else {
                                    action.actionIntent.send()
                                }
                            }
                            catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                }
                v1
            }

            view.findViewById<TextView>(R.id.source).apply {
                text = notification.source
                setTextColor(notification.color)
            }
            view.findViewById<ImageView>(R.id.source_icon).setImageDrawable(notification.sourceIcon)
            view.findViewById<TextView>(R.id.title).run {
                text = notification.title
                setTextColor(Settings["notif:title_color", -0xeeeded])
            }
            view.findViewById<TextView>(R.id.txt).run {
                text = notification.text
                setTextColor(Settings["notif:text_color", -0xdad9d9])
            }

            if (notification.image != null) {
                view.findViewById<ImageView>(R.id.iconimg).setImageDrawable(notification.image)
            }

            view.setOnClickListener { notification.open() }
            view.setOnLongClickListener(Gestures::onLongPress)
            holder.linearLayout.addView(retView)
        }
    }

    fun update(groups: ArrayList<ArrayList<NotificationItem>>) {
        this.groups = groups
        notifyDataSetChanged()
    }
}
