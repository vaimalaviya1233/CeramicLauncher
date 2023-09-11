package one.zagura.CeramicLauncher.ui

import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.drawable.NonDrawable
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.data.items.Folder
import one.zagura.CeramicLauncher.data.items.LauncherItem
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Dock
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.util.vibrate
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager

object ItemLongPress {

    const val REMOVE = 0
    const val HIDE = 1
    const val UNHIDE = 2

    var currentPopup: PopupWindow? = null
    fun makePopupWindow(context: Context, item: LauncherItem, onEdit: ((View) -> Unit)?, onRemove: ((View) -> Unit)?, onInfo: ((View) -> Unit)?, removeFunction: Int): PopupWindow {
        val content = if (item is App && item.getShortcuts(context)!!.isNotEmpty()) {
            val shortcuts = item.getShortcuts(context)
            val c = LayoutInflater.from(context).inflate(R.layout.app_long_press_menu_w_shortcuts, null)
            val recyclerView: RecyclerView = c.findViewById(R.id.shortcuts)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = ShortcutAdapter(context, shortcuts!!)
            c
        } else LayoutInflater.from(context).inflate(R.layout.app_long_press_menu, null)
        val window = PopupWindow(content, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true)
        currentPopup = window
        window.setOnDismissListener { currentPopup = null }
        window.setBackgroundDrawable(NonDrawable())

        val title = content.findViewById<TextView>(R.id.title)
        val removeButton = content.findViewById<View>(R.id.removeBtn)
        val editButton = content.findViewById<View>(R.id.editbtn)
        val propertiesButton = content.findViewById<View>(R.id.appinfobtn)

        title.text = item.label

        if (removeButton is TextView) {
            removeButton.compoundDrawableTintList = ColorStateList.valueOf(Global.getPastelAccent())
            (propertiesButton as TextView).compoundDrawableTintList = ColorStateList.valueOf(Global.getPastelAccent())
            (editButton as TextView).compoundDrawableTintList = ColorStateList.valueOf(Global.getPastelAccent())
            when (removeFunction) {
                HIDE -> {
                    removeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_hide, 0, 0, 0)
                    removeButton.setText(R.string.hide)
                }
                UNHIDE -> {
                    removeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_hide, 0, 0, 0)
                    removeButton.setText(R.string.unhide)
                }
            }
        } else {
            (removeButton as ImageView).imageTintList = ColorStateList.valueOf(Global.getPastelAccent())
            (propertiesButton as ImageView).imageTintList = ColorStateList.valueOf(Global.getPastelAccent())
            (editButton as ImageView).imageTintList = ColorStateList.valueOf(Global.getPastelAccent())
            when (removeFunction) {
                HIDE, UNHIDE -> {
                    removeButton.setImageResource(R.drawable.ic_hide)
                }
            }
        }

        if (onEdit == null) editButton.visibility = View.GONE else {
            editButton.setOnClickListener {
                window.dismiss()
                onEdit(it)
            }
        }

        if (onRemove == null) removeButton.visibility = View.GONE else {
            removeButton.setOnClickListener {
                window.dismiss()
                onRemove(it)
            }
        }

        if (onInfo == null) propertiesButton.visibility = View.GONE else {
            propertiesButton.setOnClickListener {
                window.dismiss()
                onInfo(it)
            }
        }

        return window
    }

    inline fun onItemLongPress(context: Context, view: View, item: LauncherItem, noinline onEdit: ((View) -> Unit)?, noinline onRemove: ((View) -> Unit)?, dockI: Int = -1, folderI: Int = -1, parentView: View = view, removeFunction: Int = REMOVE) {
        if (currentPopup == null) {
            context.vibrate()

            val icon = view.findViewById<View>(R.id.iconimg)

            val (x, y, gravity) = Tools.getPopupLocationFromView(icon)

            val realItem = if (folderI != -1 && item is Folder) item.items[folderI] else item

            val popupWindow = makePopupWindow(context, realItem, onEdit, onRemove, if (realItem is App) ({
                realItem.showProperties(context)
            }) else null, removeFunction)

            if (!Settings["locked", false]) {
                popupWindow.isFocusable = false // !
                val shadow = View.DragShadowBuilder(icon)
                val clipData = if (dockI == -1) {
                    ClipData.newPlainText(realItem.toString(), "")
                } else ClipData.newPlainText(realItem.toString(), dockI.toString(16))

                icon.startDragAndDrop(clipData, shadow, view, View.DRAG_FLAG_OPAQUE or View.DRAG_FLAG_GLOBAL)

                if (dockI != -1) {
                    if (folderI != -1 && item is Folder) {
                        item.items.removeAt(folderI)
                        if (folderI < 4) {
                            item.updateIcon()
                        }
                        Dock[dockI] = if (item.items.size == 1) item.items[0] else item
                    } else {
                        Dock[dockI] = null
                    }
                }
            }

            popupWindow.showAtLocation(parentView, gravity, x, y)
        }
    }
}