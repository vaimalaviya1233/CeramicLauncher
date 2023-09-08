package one.zagura.CeramicLauncher.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.ui.customizations.FakeLauncherActivity
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Tools
import java.lang.ref.WeakReference


class Setup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup)
        Tools.appContextReference = WeakReference(applicationContext)
        Settings.init(applicationContext)
    }

    fun grantNotificationAccess(v: View) {
        if (!NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(applicationContext.packageName)) {
            applicationContext.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else Toast.makeText(this, "Notification access already granted", Toast.LENGTH_LONG).show()
    }

    fun grantStorageAccess(v: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        } else
            Toast.makeText(this, "Storage access already granted", Toast.LENGTH_LONG).show()
    }

    fun grantContactsAccess(v: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 0)
        } else
            Toast.makeText(this, "Storage access already granted", Toast.LENGTH_LONG).show()
    }

    fun done(v: View) {
        Settings.apply {
            putNotSave("init", false)
            apply()
        }
        if (!Tools.isDefaultLauncher(v.context.packageManager)) chooseLauncher()
        startActivity(Intent(this, Home::class.java))
        finish()
    }

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
