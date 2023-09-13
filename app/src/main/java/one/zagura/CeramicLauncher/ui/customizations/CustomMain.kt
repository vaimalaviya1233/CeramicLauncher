package one.zagura.CeramicLauncher.ui.customizations

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.ui.customizations.order.FeedOrderActivity
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.setting.*
import one.zagura.CeramicLauncher.util.StackTraceActivity
import one.zagura.CeramicLauncher.util.Tools
import java.lang.ref.WeakReference

class CustomMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StackTraceActivity.init(applicationContext)
        Settings.init(applicationContext)
        if (Tools.appContext == null) {
            Tools.appContextReference = WeakReference(applicationContext)
        }
        configureWindowForSettings()
        Global.customized = true
    }

    override fun onResume() {
        super.onResume()
        setSettingsContentView(R.string.settings_title) {
            clickable(
                labelId = R.string.sections,
                iconId = R.drawable.ic_sections,
                onClick = { startActivity(Intent(it.context, FeedOrderActivity::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_feed,
                iconId = R.drawable.ic_home,
                onClick = { startActivity(Intent(it.context, CustomHome::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_drawer,
                iconId = R.drawable.ic_apps,
                onClick = { startActivity(Intent(it.context, CustomDrawer::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_dock,
                iconId = R.drawable.ic_dock,
                onClick = { startActivity(Intent(it.context, CustomDock::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_folders,
                iconId = R.drawable.ic_folder,
                onClick = { startActivity(Intent(it.context, CustomFolders::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_search,
                iconId = R.drawable.ic_search,
                onClick = { startActivity(Intent(it.context, CustomSearch::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_theme,
                iconId = R.drawable.ic_droplet,
                onClick = { startActivity(Intent(it.context, CustomTheme::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_gestures,
                iconId = R.drawable.ic_other,
                onClick = { startActivity(Intent(it.context, CustomGestures::class.java)) },
            )
            clickable(
                labelId = R.string.settings_title_other,
                iconId = R.drawable.ic_other,
                onClick = { startActivity(Intent(it.context, CustomOther::class.java)) },
            )
            if (Settings["dev:enabled", false]) {
                clickable(
                    labelId = R.string.settings_title_dev,
                    iconId = R.drawable.ic_other,
                    onClick = { startActivity(Intent(it.context, CustomDev::class.java)) },
                )
            }
            clickable(
                labelId = R.string.aboutbtn,
                iconId = R.drawable.ic_info,
                onClick = { startActivity(Intent(it.context, About::class.java)) },
            )
        }
    }
}