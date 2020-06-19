package posidon.launcher.tutorial

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.customizations.FakeLauncherActivity
import posidon.launcher.customizations.IconPackPicker
import posidon.launcher.feed.news.chooser.FeedChooser
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Tools
import java.lang.ref.WeakReference


class Tutorial : AppCompatActivity() {

    private val styleButtons = intArrayOf(R.id.stylepixel, R.id.styleoneui, R.id.styleios, R.id.styleposidon)
    private var selectedStyle = -1
    private var done = false

    init {
        Tools.publicContextReference = WeakReference(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutorial1)
        Settings.init(this)
        for (i in styleButtons.indices) {
            findViewById<View>(styleButtons[i]).setOnClickListener {
                if (selectedStyle != -1)
                    findViewById<TextView>(styleButtons[selectedStyle]).apply {
                        background = getDrawable(R.drawable.button_bg_round)
                        setTextColor(0xffdddddd.toInt())
                    }
                val d = getDrawable(R.drawable.button_bg_round)!!.mutate() as GradientDrawable
                d.setColor(resources.getColor(R.color.accent))
                findViewById<TextView>(styleButtons[i]).apply {
                    background = d
                    setTextColor(0xffddeeff.toInt())
                }
                selectedStyle = i
                checkDone()
            }
        }
    }

    private fun checkDone() {
        if (selectedStyle != -1) {
            done = true
            val t = findViewById<TextView>(R.id.next)
            t.setTextColor(-0x1)
            t.setBackgroundColor(resources.getColor(R.color.accent))
            window.navigationBarColor = resources.getColor(R.color.accent)
        }
    }

    fun grantNotificationAccess(v: View) {
        if (!NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(applicationContext.packageName)) {
            applicationContext.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            checkDone()
        } else Toast.makeText(this, "Notification access already granted", Toast.LENGTH_LONG).show()
    }

    fun grantStorageAccess(v: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
            checkDone()
        } else
            Toast.makeText(this, "Storage access already granted", Toast.LENGTH_LONG).show()
    }

    fun done1(v: View) {
        if (done) Settings.apply {
            when (selectedStyle) {
                0 -> {
                    putNotSave("accent", 0x4285F4)
                    putNotSave("icshape", 1)//circle
                    putNotSave("reshapeicons", true)
                    putNotSave("dock:background_color", 0x66ffffff)
                    putNotSave("dock:columns", 5)
                    putNotSave("blur", false)
                    putNotSave("blurLayers", 1)
                    putNotSave("blurradius", 15f)
                    putNotSave("drawer:background_color", 0xdde0e0e0.toInt())
                    putNotSave("labelColor", 0xee252627.toInt())
                    putNotSave("labelsenabled", false)
                    putNotSave("icsize", 1)
                    putNotSave("dockicsize", 1)
                    putNotSave("searchcolor", 0xffffffff.toInt())
                    putNotSave("searchtxtcolor", 0xff333333.toInt())
                    putNotSave("searchhintcolor", 0xff888888.toInt())
                    putNotSave("docksearchcolor", 0xffffffff.toInt())
                    putNotSave("docksearchtxtcolor", 0xff333333.toInt())
                    putNotSave("docksearchbarenabled", true)
                    putNotSave("feed:card_radius", 15)
                    putNotSave("feed:card_bg", 0xffffffff.toInt())
                    putNotSave("feed:card_txt_color", 0xff252627.toInt())
                    putNotSave("feed:card_layout", 1)
                    putNotSave("feed:card_margin_x", 16)
                    putNotSave("notificationtitlecolor", 0xff000000.toInt())
                    putNotSave("notificationtxtcolor", 0xff888888.toInt())
                    putNotSave("notificationbgcolor", 0xffffffff.toInt())
                    putNotSave("drawer:sorting", 0)
                    apply()
                }
                1 -> {
                    putNotSave("accent", 0x4297FE)
                    putNotSave("icshape", 4)//squircle
                    putNotSave("reshapeicons", false)
                    putNotSave("dock:background_color", 0x0)
                    putNotSave("dock:columns", 4)
                    putNotSave("blur", true)
                    putNotSave("blurLayers", 2)
                    putNotSave("blurradius", 15f)
                    putNotSave("drawer:background_color", 0x55000000)
                    putNotSave("labelColor", 0xeeffffff.toInt())
                    putNotSave("labelsenabled", false)
                    putNotSave("icsize", 2)
                    putNotSave("dockicsize", 2)
                    putNotSave("searchcolor", 0x33000000)
                    putNotSave("searchtxtcolor", 0xffffffff.toInt())
                    putNotSave("searchhintcolor", 0xffffffff.toInt())
                    putNotSave("docksearchcolor", 0xffffffff.toInt())
                    putNotSave("docksearchtxtcolor", 0xff000000.toInt())
                    putNotSave("docksearchbarenabled", false)
                    putNotSave("feed:card_radius", 25)
                    putNotSave("feed:card_bg", 0xffffffff.toInt())
                    putNotSave("feed:card_txt_color", 0xff252627.toInt())
                    putNotSave("feed:card_layout", 2)
                    putNotSave("feed:card_margin_x", 0)
                    putNotSave("notificationtitlecolor", 0xff000000.toInt())
                    putNotSave("notificationtxtcolor", 0xff000000.toInt())
                    putNotSave("notificationbgcolor", 0xffffffff.toInt())
                    putNotSave("drawer:sorting", 0)
                    apply()
                }
                2 -> {
                    putNotSave("accent", 0x4DD863)
                    putNotSave("icshape", 2)//roundrect
                    putNotSave("reshapeicons", true)
                    putNotSave("dock:background_color", 0x66eeeeee)
                    putNotSave("dock:columns", 4)
                    putNotSave("blur", true)
                    putNotSave("blurLayers", 3)
                    putNotSave("blurradius", 15f)
                    putNotSave("drawer:background_color", 0x88111111.toInt())
                    putNotSave("labelColor", 0xeeeeeeee.toInt())
                    putNotSave("labelsenabled", false)
                    putNotSave("icsize", 1)
                    putNotSave("dockicsize", 1)
                    putNotSave("searchcolor", 0x33000000)
                    putNotSave("searchtxtcolor", 0xffffffff.toInt())
                    putNotSave("searchhintcolor", 0xffffffff.toInt())
                    putNotSave("docksearchcolor", 0xffffffff.toInt())
                    putNotSave("docksearchtxtcolor", 0xff000000.toInt())
                    putNotSave("docksearchbarenabled", false)
                    putNotSave("feed:card_radius", 25)
                    putNotSave("feed:card_bg", 0xdd000000.toInt())
                    putNotSave("feed:card_txt_color", 0xddffffff.toInt())
                    putNotSave("feed:card_layout", 0)
                    putNotSave("feed:card_margin_x", 16)
                    putNotSave("notificationtitlecolor", 0xdd000000.toInt())
                    putNotSave("notificationtxtcolor", 0x88000000.toInt())
                    putNotSave("notificationbgcolor", 0xa8eeeeee.toInt())
                    putNotSave("drawer:sorting", 0)
                    apply()
                }
            }
            setContentView(R.layout.tutorial2)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                findViewById<View>(R.id.storagecard).visibility = View.GONE
            }
        }
    }

    fun done2(v: View) {
        setContentView(R.layout.tutorial3)
        findViewById<Switch>(R.id.enableNews).setOnCheckedChangeListener { _, checked -> Settings.apply {
            putNotSave("feed:enabled", checked)
            apply()
        }}
        Tools.updateNavbarHeight(this)
    }

    fun done3(v: View) {
        Settings.apply {
            putNotSave("dock", when {
                Tools.isInstalled("org.mozilla.fenix", packageManager) -> "org.mozilla.fenix/org.mozilla.fenix.App"
                Tools.isInstalled("org.mozilla.firefox", packageManager) -> "org.mozilla.firefox/org.mozilla.firefox.App"
                else -> "com.android.chrome/com.google.android.apps.chrome.Main"
            } + '\n' + when {
                Tools.isInstalled("social.openbook.app", packageManager) -> "social.openbook.app/social.openbook.app.MainActivity"
                Tools.isInstalled("com.twitter.android", packageManager) -> "com.twitter.android/com.twitter.android.StartActivity"
                Tools.isInstalled("com.discord", packageManager) -> "com.discord/com.discord.app.AppActivity\$Main"
                Tools.isInstalled("com.trello", packageManager) -> "com.trello/com.trello.home.HomeActivity"
                Tools.isInstalled("com.topjohbwu.magisk", packageManager) -> "com.topjohbwu.magisk/a.c"
                Tools.isInstalled("com.google.android.apps.playconsole", packageManager) -> "com.google.android.apps.playconsole/com.google.android.apps.playconsole.activity.MainAndroidActivity"
                else -> ""
            } + '\n' + when {
                Tools.isInstalled("com.oneplus.camera", packageManager) -> "com.oneplus.camera/com.oneplus.camera.OPCameraActivity"
                Tools.isInstalled("com.oneplus.contacts", packageManager) -> "com.oneplus.contacts/com.oneplus.contacts.activities.OPPeopleActivity"
                else -> ""
            } + '\n' + when {
                Tools.isInstalled("org.thunderdog.challegram", packageManager) -> "org.thunderdog.challegram/org.thunderdog.challegram.MainActivity"
                Tools.isInstalled("org.telegram.messenger", packageManager) -> "org.telegram.messenger/org.telegram.ui.LaunchActivity"
                Tools.isInstalled("com.whatsapp", packageManager) -> "com.whatsapp/com.whatsapp.Main"
                Tools.isInstalled("com.oneplus.mms", packageManager) -> "com.oneplus.mms/com.android.mms.ui.ConversationList"
                else -> "com.android.mms/com.android.mms.ui.ConversationList"
            } + '\n' + when {
                Tools.isInstalled("com.netflix.mediaclient", packageManager) -> "com.netflix.mediaclient/com.netflix.mediaclient.ui.launch.UIWebViewActivity"
                Tools.isInstalled("com.hbo.android.app", packageManager) -> "com.hbo.android.app/com.hbo.android.app.bootstrap.ui.StartupActivity"
                Tools.isInstalled("com.aspiro.tidal", packageManager) -> "com.aspiro.tidal/com.aspiro.wamp.LoginFragmentActivity"
                else -> ""
            })
            apply()
        }
        setContentView(R.layout.tutorial4)
    }

    fun done4(v: View) {
        Settings.apply {
            putNotSave("init", false)
            apply()
        }
        if (!Tools.isDefaultLauncher) chooseLauncher()
        startActivity(Intent(this, Main::class.java))
        finish()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fadein, R.anim.appexit)
    }

    override fun onResume() {
        super.onResume()
        checkDone()
    }

    fun chooseFeeds(v: View) { startActivity(Intent(this, FeedChooser::class.java)) }
    fun iconPackSelector(v: View) { startActivity(Intent(this, IconPackPicker::class.java)) }

    private fun chooseLauncher() {
        val packageManager: PackageManager = packageManager
        val componentName = ComponentName(this, FakeLauncherActivity::class.java)
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        selector.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(selector)
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP)
    }
}
