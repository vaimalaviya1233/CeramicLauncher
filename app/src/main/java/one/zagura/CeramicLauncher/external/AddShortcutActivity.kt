package one.zagura.CeramicLauncher.external

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.items.PinnedShortcut
import one.zagura.CeramicLauncher.tools.Dock
import one.zagura.CeramicLauncher.view.drawer.DockView

class AddShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val shortcut = launcherApps.getPinItemRequest(intent).shortcutInfo
        val hasHostPermission = launcherApps.hasShortcutHostPermission()
        if (shortcut == null || !hasHostPermission) {
            Log.e("Ceramic Launcher", "no shortcut host permission")
            return
        }
        val s = PinnedShortcut(shortcut, this)

        setContentView(R.layout.add_shortcut_activity)

        val dock = findViewById<DockView>(R.id.dock)
        dock.searchBar.visibility = View.GONE
        dock.battery.visibility = View.GONE

        dock.loadApps()
        dock.onItemClick = { context, view, i, item ->
            PinnedShortcut.pin(this, s)
            Dock.add(s, i)
            Home.instance.drawer.dock.loadApps()
        }
    }
}