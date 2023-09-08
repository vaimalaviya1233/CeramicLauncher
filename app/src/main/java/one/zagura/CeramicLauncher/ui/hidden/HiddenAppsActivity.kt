package one.zagura.CeramicLauncher.ui.hidden

import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.ui.ItemLongPress
import one.zagura.CeramicLauncher.ui.ItemLongPress.UNHIDE
import one.zagura.CeramicLauncher.util.storage.Settings
import kotlin.math.abs

class HiddenAppsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hidden_apps_activity)
        val grid = findViewById<GridView>(R.id.grid)
        grid.numColumns = Settings["drawer:columns", 5]
        grid.adapter = AppsAdapter(this, App.hidden.toTypedArray())
        grid.setOnItemClickListener { _, view, i, _ ->
            App.hidden[i].open(this, view)
        }
        grid.setOnItemLongClickListener { _, view, i, _ ->
            val app = App.hidden[i]
            ItemLongPress.onItemLongPress(this, view, app, null, onRemove = {
                App.hidden.remove(app)
                grid.adapter = AppsAdapter(this, App.hidden.toTypedArray())
                app.setUnhidden()
                Global.shouldSetApps = true
            }, removeFunction = UNHIDE)
            true
        }
        window.decorView.findViewById<View>(android.R.id.content).setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val icon = event.localState as View
                    val location = IntArray(2)
                    icon.getLocationOnScreen(location)
                    val y = abs(event.y - location[1])
                    if (y > icon.height / 3.5f) {
                        ItemLongPress.currentPopup?.dismiss()
                        finish()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_STARTED -> {
                    (event.localState as View).visibility = View.INVISIBLE
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    (event.localState as View).visibility = View.VISIBLE
                    ItemLongPress.currentPopup?.isFocusable = true
                    ItemLongPress.currentPopup?.update()
                    true
                }
                else -> false
            }
        }
    }
}