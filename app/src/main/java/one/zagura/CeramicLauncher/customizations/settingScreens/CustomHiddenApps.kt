package one.zagura.CeramicLauncher.customizations.settingScreens

import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserManager
import one.zagura.CeramicLauncher.customizations.settingScreens.general.AppTickingActivity
import one.zagura.CeramicLauncher.items.App
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.tools.Sort

class CustomHiddenApps : AppTickingActivity() {

    override fun getApps(): List<App> {
        val apps = ArrayList<App>()

        val userManager = getSystemService(Context.USER_SERVICE) as UserManager
        val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (profile in userManager.userProfiles) {
            val appList = launcherApps.getActivityList(null, profile)
            for (i in appList.indices) {
                App[appList[i].applicationInfo.packageName, appList[i].name, profile.hashCode()]?.let { apps.add(it) }
            }
        }

        Sort.labelSort(apps)
        return apps
    }

    override fun isTicked(app: App) = Settings["app:$app:hidden", false]
    override fun setTicked(app: App, isTicked: Boolean) {
        Settings["app:$app:hidden"] = isTicked
    }
}