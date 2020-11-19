package posidon.launcher.customizations

import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserManager
import posidon.launcher.Home
import posidon.launcher.items.App
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Sort

class CustomHiddenApps : AppTickingActivity() {

    override fun getApps(): List<App> {
        val apps = ArrayList<App>()

        val userManager = Home.instance.getSystemService(Context.USER_SERVICE) as UserManager
        for (profile in userManager.userProfiles) {
            val appList = (getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps).getActivityList(null, profile)
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