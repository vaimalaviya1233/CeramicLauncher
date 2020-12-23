package posidon.launcher.items.users

import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import posidon.launcher.Global
import posidon.launcher.Home
import posidon.launcher.R
import posidon.launcher.external.Kustom
import posidon.launcher.items.App
import posidon.launcher.items.Folder
import posidon.launcher.items.LauncherItem
import posidon.launcher.storage.Settings
import posidon.launcher.tools.*
import posidon.launcher.view.LinearLayoutManager

object ItemLongPress {

    var currentPopup: PopupWindow? = null
    fun makePopupWindow(context: Context, onEdit: (View) -> Unit, onRemove: ((View) -> Unit)?, onInfo: ((View) -> Unit)?, item: LauncherItem): PopupWindow {

        if (Settings["kustom:variables:enable", false]) {
            Kustom["screen"] = "popup"
        }

        val color = item.getColor()
        val txtColor = if (ColorTools.useDarkText(color)) -0xeeeded else -0x1

        val content = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && item is App && item.getShortcuts(context)!!.isNotEmpty()) {
            val shortcuts = item.getShortcuts(context)
            val c = LayoutInflater.from(context).inflate(R.layout.app_long_press_menu_w_shortcuts, null)
            val recyclerView: RecyclerView = c.findViewById(R.id.shortcuts)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = ShortcutAdapter(context, shortcuts!!, txtColor)
            val bg = ShapeDrawable()
            val r = 18.dp
            bg.shape = RoundRectShape(floatArrayOf(0f, 0f, 0f, 0f, r, r, r, r), null, null)
            bg.paint.color = 0x33000000
            recyclerView.background = bg
            c
        } else LayoutInflater.from(context).inflate(R.layout.app_long_press_menu, null)
        val window = PopupWindow(content, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true)
        currentPopup = window
        window.setOnDismissListener { currentPopup = null }
        window.setBackgroundDrawable(ColorDrawable(0x0))

        val title = content.findViewById<TextView>(R.id.title)
        val removeBtn = content.findViewById<View>(R.id.removeBtn)
        val editBtn = content.findViewById<View>(R.id.editbtn)
        val appinfobtn = content.findViewById<View>(R.id.appinfobtn)

        title.text = item.label
        title.setTextColor(txtColor)

        content.findViewById<View>(R.id.bg).background = run {
            val bg = ShapeDrawable()
            val r = 18.dp
            bg.shape = RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null)
            bg.paint.color = color
            bg
        }

        if (removeBtn is TextView) {
            removeBtn.setTextColor(txtColor)
            (appinfobtn as TextView).setTextColor(txtColor)
            (editBtn as TextView).setTextColor(txtColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                removeBtn.compoundDrawableTintList = ColorStateList.valueOf(txtColor)
                appinfobtn.compoundDrawableTintList = ColorStateList.valueOf(txtColor)
                editBtn.compoundDrawableTintList = ColorStateList.valueOf(txtColor)
            }
        } else {
            (removeBtn as ImageView).imageTintList = ColorStateList.valueOf(txtColor)
            (appinfobtn as ImageView).imageTintList = ColorStateList.valueOf(txtColor)
            (editBtn as ImageView).imageTintList = ColorStateList.valueOf(txtColor)
        }

        editBtn.setOnClickListener {
            window.dismiss()
            onEdit(it)
        }

        if (onRemove == null) removeBtn.visibility = View.GONE else {
            removeBtn.setOnClickListener {
                window.dismiss()
                onRemove(it)
            }
        }

        if (onInfo == null) appinfobtn.visibility = View.GONE else {
            appinfobtn.setOnClickListener {
                window.dismiss()
                onInfo(it)
            }
        }

        return window
    }

    fun showPopupWindow(context: Context, view: View, item: LauncherItem, onEdit: (View) -> Unit, onRemove: ((View) -> Unit)?, dockI: Int = -1, folderI: Int = -1, parentView: View = view) {
        if (currentPopup == null) {
            context.vibrate()

            val icon = view.findViewById<View>(R.id.iconimg)

            val location = IntArray(2).also {
                icon.getLocationOnScreen(it)
            }

            var gravity: Int

            val x = if (location[0] > Device.displayWidth / 2) {
                gravity = Gravity.END
                Device.displayWidth - location[0] - icon.measuredWidth
            } else {
                gravity = Gravity.START
                location[0]
            }

            val y = if (location[1] < Device.displayHeight / 2) {
                gravity = gravity or Gravity.TOP
                location[1] + icon.measuredHeight + 4.dp.toInt()
            } else {
                gravity = gravity or Gravity.BOTTOM
                Device.displayHeight - location[1] + 4.dp.toInt() + Tools.navbarHeight
            }

            val realItem = if (folderI != -1 && item is Folder) item.items[folderI] else item

            val popupWindow = makePopupWindow(context, onEdit, onRemove, if (item is App) ({
                val color = item.getColor()
                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)
                if (hsv[2] > 0.5f) {
                    hsv[2] = 0.5f
                }
                item.showProperties(context, Color.HSVToColor(hsv))
            }) else null, realItem)

            if (!Settings["locked", false]) {
                popupWindow.isFocusable = false // !
                val shadow = View.DragShadowBuilder(icon)
                val clipData = if (dockI == -1) {
                    ClipData.newPlainText(realItem.toString(), "")
                } else ClipData.newPlainText(realItem.toString(), dockI.toString(16))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    icon.startDragAndDrop(clipData, shadow, view, View.DRAG_FLAG_OPAQUE or View.DRAG_FLAG_GLOBAL)
                } else {
                    icon.startDrag(clipData, shadow, view, 0)
                }

                if (dockI != -1) {
                    if (folderI != -1 && item is Folder) {
                        item.items.removeAt(folderI)
                        Dock[dockI] = if (item.items.size == 1) item.items[0] else item
                    } else {
                        Dock[dockI] = null
                    }
                }
            }

            popupWindow.showAtLocation(parentView, gravity, x, y)
        }
    }

	fun dock(context: Context, app: App, i: Int) = View.OnLongClickListener { view ->
        showPopupWindow(context, view, app, onRemove = {
            Dock[i] = null
            Home.instance.setDock()
        }, onEdit = { app.showAppEditDialog(context, it) }, dockI = i)
        true
    }

    fun insideFolder(context: Context, app: App, i: Int, v: View, folderI: Int, folderWindow: PopupWindow, folder: Folder) = View.OnLongClickListener { view ->
        showPopupWindow(context, view, folder, onRemove = {
            folderWindow.dismiss()
            folder.items.removeAt(folderI)
            Dock[i] = if (folder.items.size == 1) folder.items[0] else folder
            Home.instance.setDock()
        }, onEdit = { app.showAppEditDialog(context, v) }, dockI = i, folderI = folderI, parentView = v)
        true
    }

    fun olddrawer(context: Context) = OnItemLongClickListener { _, view, position, _ ->
        val app = Global.apps[position]
        showPopupWindow(context, view, app, { app.showAppEditDialog(context, it) }, null)
        true
    }

    fun drawer(context: Context, app: App) = View.OnLongClickListener {
        showPopupWindow(context, it, app, { app.showAppEditDialog(context, it) }, null)
        true
    }
}