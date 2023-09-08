package one.zagura.CeramicLauncher.provider

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import androidx.palette.graphics.Palette
import io.posidon.android.conveniencelib.drawable.toBitmap
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.isUserRunning
import one.zagura.CeramicLauncher.util.theme.Icons
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class AppLoader(
    private val context: Context,
    private val onEnd: () -> Unit
) {

    private var tmpApps = ArrayList<App>()
    private val tmpAppSections = ArrayList<ArrayList<App>>()
    private var tmpHidden = ArrayList<App>()
    private var lock = ReentrantLock()

    fun execute() { thread(isDaemon = true, block = ::run) }
    fun run() {
        lock.lock()

        val userManager = Home.instance.getSystemService(Context.USER_SERVICE) as UserManager
        val threads = LinkedList<Thread>()

        for (profile in userManager.userProfiles) {

            val appList = (context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps).getActivityList(null, profile)

            for (i in appList.indices) {

                val packageName = appList[i].applicationInfo.packageName
                val name = appList[i].name

                var label = Settings["$packageName/$name?label", appList[i].label.toString()]
                if (label.isEmpty()) {
                    Settings["$packageName/$name?label"] = appList[i].label.toString()
                    label = appList[i].label.toString()
                    if (label.isEmpty()) {
                        label = packageName
                    }
                }

                val app = App(packageName, name, profile, label)

                threads.add(thread (isDaemon = true) {
                    app.icon = appList[i].getIcon(0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        app.icon = Icons.generateAdaptiveIcon(app.icon!!)
                    }
                    if (!context.isUserRunning(profile)) {
                        app.icon!!.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                            setSaturation(0f)
                        })
                    }
                    context.packageManager.getUserBadgedIcon(app.icon!!, profile)
                })

                putInSecondMap(app)
                if (Settings["app:$app:hidden", false]) {
                    tmpHidden.add(app)
                } else {
                    tmpApps.add(app)
                }
            }
        }

        for (t in threads) {
            t.join()
        }

        threads.clear()

        if (Settings["drawer:sorting", 0] == 1) tmpApps.sortWith { o1, o2 ->
            val iHsv = floatArrayOf(0f, 0f, 0f)
            val jHsv = floatArrayOf(0f, 0f, 0f)
            Color.colorToHSV(Palette.from(o1.icon!!.toBitmap()).generate().getVibrantColor(0xff252627.toInt()), iHsv)
            Color.colorToHSV(Palette.from(o2.icon!!.toBitmap()).generate().getVibrantColor(0xff252627.toInt()), jHsv)
            iHsv[0].compareTo(jHsv[0])
        }
        else tmpApps.sortWith { o1, o2 ->
            o1.label.compareTo(o2.label, ignoreCase = true)
        }

        var currentChar = tmpApps[0].label[0].uppercaseChar()
        var currentSection = ArrayList<App>().also { tmpAppSections.add(it) }
        for (app in tmpApps) {
            if (app.label.startsWith(currentChar, ignoreCase = true)) {
                currentSection.add(app)
            }
            else currentSection = ArrayList<App>().apply {
                add(app)
                tmpAppSections.add(this)
                currentChar = app.label[0].uppercaseChar()
            }
        }

        lock.unlock()

        Home.instance.runOnUiThread {
            App.onFinishLoad(tmpApps, tmpAppSections, tmpHidden, appsByName)
            onEnd()
        }

        System.gc()
    }

    class Callback(
        val context: Context,
        val onAppLoaderEnd: () -> Unit
    ) : LauncherApps.Callback() {
        override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle?, replacing: Boolean) = AppLoader(context, onAppLoaderEnd).execute()
        override fun onPackageChanged(packageName: String, user: UserHandle?) = AppLoader(context, onAppLoaderEnd).execute()
        override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle?, replacing: Boolean) = AppLoader(context, onAppLoaderEnd).execute()
        override fun onPackageAdded(packageName: String, user: UserHandle?) = AppLoader(context, onAppLoaderEnd).execute()
        override fun onPackageRemoved(packageName: String, user: UserHandle?) {
            Global.apps.removeAll { it.packageName == packageName }
            val iter = Global.appSections.iterator()
            for (section in iter) {
                section.removeAll {
                    it.packageName == packageName
                }
                if (section.isEmpty()) {
                    iter.remove()
                }
            }
            App.removePackage(packageName)
            onAppLoaderEnd()
        }
    }

    private var appsByName = HashMap<String, ArrayList<App>>()
    private fun putInSecondMap(app: App) {
        val list = appsByName[app.packageName]
        if (list == null) {
            appsByName[app.packageName] = arrayListOf(app)
            return
        }
        val thisAppI = list.indexOfFirst {
            it.name == app.name && it.userHandle.hashCode() == app.userHandle.hashCode()
        }
        if (thisAppI == -1) {
            list.add(app)
            return
        }
        list[thisAppI] = app
    }
}