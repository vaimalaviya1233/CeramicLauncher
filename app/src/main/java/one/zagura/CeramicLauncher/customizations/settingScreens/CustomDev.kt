package one.zagura.CeramicLauncher.customizations.settingScreens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.setting.card
import one.zagura.CeramicLauncher.view.setting.configureWindowForSettings
import one.zagura.CeramicLauncher.view.setting.setSettingsContentView
import one.zagura.CeramicLauncher.view.setting.switch

class CustomDev : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_dev) {
            card {
                switch(
                    labelId = R.string.setting_show_component,
                    iconId = R.drawable.ic_visible,
                    key = "dev:show_app_component",
                    default = false,
                )
                switch(
                    labelId = R.string.hide_crash_logs,
                    iconId = R.drawable.ic_label,
                    key = "dev:hide_crash_logs",
                    default = true,
                )
            }
        }
        Global.customized = true
    }
}