package one.zagura.CeramicLauncher.data.items

import android.app.ActivityOptions
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Drawable
import android.os.Process
import android.view.View
import one.zagura.CeramicLauncher.R

class StaticShortcut(
    val packageName: String,
    val id: String,
    override val label: String,
    override val icon: Drawable?,
    val app: App,
) : LauncherItem() {

    override fun open(context: Context, view: View, dockI: Int) {
        val info = getShortcutInfo(context) ?: return
        val launcherApps = context.getSystemService(LauncherApps::class.java)
        launcherApps.startShortcut(info, null, ActivityOptions.makeCustomAnimation(context, R.anim.appopen, R.anim.home_exit).toBundle())
    }

    fun getShortcutInfo(context: Context): ShortcutInfo? {
        val launcherApps = context.getSystemService(LauncherApps::class.java)
        val query = LauncherApps.ShortcutQuery().setPackage(packageName).setShortcutIds(listOf(id))
        return launcherApps.getShortcuts(query, Process.myUserHandle())?.firstOrNull()
    }

    override fun toString() = "shortcut:$packageName/$id"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as StaticShortcut
        if (id != other.id) return false
        if (packageName != other.packageName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

}