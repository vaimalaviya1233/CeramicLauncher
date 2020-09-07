package posidon.launcher.feed.news.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.feed.news.chooser.suggestions.SuggestionsAdapter
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Tools
import posidon.launcher.tools.dp
import posidon.launcher.tools.getStatusBarHeight
import posidon.launcher.tools.vibrate
import posidon.launcher.view.GridLayoutManager

class FeedChooser : AppCompatActivity() {

    private lateinit var grid: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed_chooser)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        grid = findViewById(R.id.grid)
        grid.layoutManager = GridLayoutManager(this, 2)
        val padding = 4.dp.toInt()
        grid.setPadding(padding, getStatusBarHeight(), padding, Tools.navbarHeight + padding)

        val feedUrls = ArrayList(Settings["feedUrls", defaultSources].split("|"))
        if (feedUrls.size == 1 && feedUrls[0].replace(" ", "") == "") {
            feedUrls.removeAt(0)
            Settings.putNotSave("feedUrls", "")
            Settings.apply()
        }

        grid.adapter = FeedChooserAdapter(this@FeedChooser, feedUrls)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.backgroundTintList = ColorStateList.valueOf(Main.accentColor and 0x00ffffff or 0x33000000)
        fab.imageTintList = ColorStateList.valueOf(Main.accentColor)
        fab.setOnClickListener { addSource(this, feedUrls, grid.adapter!!) }
        (fab.layoutParams as FrameLayout.LayoutParams).bottomMargin = 20.dp.toInt() + Tools.navbarHeight
    }

    companion object {
        const val defaultSources = "androidpolice.com/feed"

        @SuppressLint("ClickableViewAccessibility")
        private inline fun addSource(context: Context, feedUrls: ArrayList<String>, adapter: RecyclerView.Adapter<*>) {
            context.vibrate()
            val dialog = BottomSheetDialog(context, R.style.bottomsheet)
            dialog.setContentView(R.layout.feed_chooser_option_edit_dialog)
            dialog.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottom_sheet)
            val input = dialog.findViewById<EditText>(R.id.title)!!
            dialog.findViewById<ExpandableListView>(R.id.list)!!.apply {
                visibility = View.VISIBLE
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN && this.canScrollVertically(-1))
                        this.requestDisallowInterceptTouchEvent(true)
                    false
                }
                setAdapter(SuggestionsAdapter(context) {
                    input.setText(it.url, TextView.BufferType.EDITABLE)
                })
            }
            dialog.findViewById<TextView>(R.id.done)!!.apply {
                setTextColor(Main.accentColor)
                backgroundTintList = ColorStateList.valueOf(Main.accentColor and 0x00ffffff or 0x33000000)
                setOnClickListener {
                    dialog.dismiss()
                    feedUrls.add(input.text.toString().replace('|', ' '))
                    adapter.notifyDataSetChanged()
                    Settings.putNotSave("feedUrls", feedUrls.joinToString("|"))
                    Settings.apply()
                }
            }
            dialog.findViewById<TextView>(R.id.remove)!!.visibility = View.GONE
            dialog.show()
        }
    }
}