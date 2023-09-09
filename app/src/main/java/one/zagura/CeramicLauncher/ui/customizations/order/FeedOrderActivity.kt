package one.zagura.CeramicLauncher.ui.customizations.order

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.posidon.android.conveniencelib.getStatusBarHeight
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.external.widgets.Widget
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.ui.view.feed.Feed
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager

class FeedOrderActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView

    val sections = Feed.getSectionsFromSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed_order)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(false)
        else window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        recycler = findViewById<RecyclerView>(R.id.recycler).apply {
            setPadding(0, getStatusBarHeight(), 0, Tools.navbarHeight)

            layoutManager = LinearLayoutManager(this@FeedOrderActivity, RecyclerView.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = OrderAdapter(sections)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter!!
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                sections.add(to, sections.removeAt(from))
                Settings.apply()

                adapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        }).attachToRecyclerView(recycler)

        with(findViewById<FloatingActionButton>(R.id.fab)) {
            backgroundTintList = ColorStateList.valueOf(Global.getPastelAccent())
            imageTintList = ColorStateList.valueOf(context.getColor(R.color.ui_background))
            (layoutParams as FrameLayout.LayoutParams).bottomMargin = 20.dp.toPixels(context) + Tools.navbarHeight
            setOnClickListener {
                Feed.selectFeedSectionToAdd(this@FeedOrderActivity, ::onItemSelect)
            }
        }

        Global.customized = true
    }

    private fun onItemSelect(it: String) {
        sections.add(0, it)
        Settings.apply()
        recycler.adapter!!.notifyItemInserted(0)
    }

    override fun onPause() {
        Global.customized = true
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val w = Widget.handleActivityResult(this, requestCode, resultCode, data)
        w?.let { it1 -> onItemSelect(it1.toString()) }
        super.onActivityResult(requestCode, resultCode, data)
    }
}