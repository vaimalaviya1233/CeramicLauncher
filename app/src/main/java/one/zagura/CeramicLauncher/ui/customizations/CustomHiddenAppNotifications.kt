package one.zagura.CeramicLauncher.ui.customizations

import one.zagura.CeramicLauncher.ui.customizations.general.AppTickingActivity
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Sort
import one.zagura.CeramicLauncher.util.theme.Icons

class CustomHiddenAppNotifications : AppTickingActivity() {

    override fun getApps(): List<App> {
        val apps = ArrayList<App>()

        val pm = packageManager
        val pacslist = pm.getInstalledPackages(0)
        for (i in pacslist.indices) {
            apps.add(App(pacslist[i].packageName, pacslist[i].activities?.firstOrNull()?.name ?: "", label = pacslist[i].applicationInfo.loadLabel(pm).toString()).apply {
                icon = pacslist[i].applicationInfo.loadIcon(pm)
                icon = Icons.generateAdaptiveIcon(icon!!)
            })
        }

        Sort.labelSort(apps)
        return apps
    }

    override fun isTicked(app: App): Boolean = Settings["notif:ex:${app.packageName}", false]
    override fun setTicked(app: App, isTicked: Boolean) {
        Settings["notif:ex:${app.packageName}"] = isTicked
    }
}