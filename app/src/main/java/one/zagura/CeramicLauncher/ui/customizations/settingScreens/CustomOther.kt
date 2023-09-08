package one.zagura.CeramicLauncher.ui.customizations.settingScreens

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.ui.customizations.FakeLauncherActivity
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.setting.*
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class CustomOther : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_other) {
            switch(
                labelId = R.string.settingshidestatus,
                iconId = R.drawable.ic_visible,
                key = "hidestatus",
                default = false,
            )
            switch(
                labelId = R.string.minimal_statusbar,
                iconId = R.drawable.ic_visible,
                key = "mnmlstatus",
                default = false,
            )
            switch(
                labelId = R.string.ignore_navbar_height,
                iconId = R.drawable.ic_visible,
                key = "ignore_navbar",
                default = false,
            )
            title(R.string.haptic_feedback)
            numberSeekBar(
                labelId = R.string.duration,
                key = "hapticfeedback",
                default = 14,
                max = 100,
            )
            separator()
            clickable(
                labelId = R.string.setting_title_hide_apps,
                iconId = R.drawable.ic_visible,
            ) {
                it.context.startActivity(Intent(it.context, CustomHiddenApps::class.java))
            }
            spinner(
                labelId = R.string.app_open_animation,
                iconId = R.drawable.ic_play,
                key = "anim:app_open",
                default = 0,
                array = R.array.animationNames,
            )
            clickable(
                labelId = R.string.choose_a_launcher,
                iconId = R.drawable.ic_home,
                onClick = ::chooseLauncher,
            )
            switch(
                labelId = R.string.lock_home,
                iconId = R.drawable.ic_lock,
                key = "locked",
                default = false,
            )
            separator()
            clickable(
                labelId = R.string.mk_backup,
                iconId = R.drawable.ic_save,
                onClick = ::mkBackup,
            )
            clickable(
                labelId = R.string.use_backup,
                iconId = R.drawable.ic_save,
                onClick = ::useBackup,
            )
            separator()
            clickable(
                labelId = R.string.force_stop_launcher,
                iconId = R.drawable.ic_home,
                onClick = ::stop,
            )
        }
        Global.customized = true
    }

    override fun onPause() {
        Global.customized = true
        Settings.apply {
            apply()
        }
        super.onPause()
    }

    fun openHideApps(v: View) = startActivity(Intent(this, CustomHiddenApps::class.java))
    fun stop(v: View): Unit = exitProcess(0)
    fun mkBackup(v: View) = Settings.saveBackup()
    fun useBackup(v: View) {
        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                try { data?.data?.let {
                    Settings.restoreFromBackup(it)
                    Toast.makeText(this, "Backup restored!", Toast.LENGTH_LONG).show()
                }}
                catch (e: FileNotFoundException) { e.printStackTrace() }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun chooseLauncher(v: View) {
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