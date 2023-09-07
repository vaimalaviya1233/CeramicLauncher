package one.zagura.CeramicLauncher.customizations.settingScreens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.setting.*

class CustomDock : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_dock) {
            card {
                numberSeekBar(
                    labelId = R.string.columns,
                    key = "dock:columns",
                    default = 5,
                    max = 7,
                    startsWith1 = true,
                )
                numberSeekBar(
                    labelId = R.string.settings_rows,
                    key = "dock:rows",
                    default = 2,
                    max = 5,
                    startsWith1 = true,
                )
                numberSeekBar(
                    labelId = R.string.iconSize,
                    key = "dock:icons:size",
                    default = 74,
                    max = 96,
                    startsWith1 = true,
                )
            }
            card {
                color(
                    labelId = R.string.background,
                    iconId = R.drawable.ic_color,
                    key = "dock:background_color",
                    default = 0xff242424.toInt(),
                )
                spinner(
                    labelId = R.string.background_type,
                    iconId = R.drawable.ic_shapes,
                    key = "dock:background_type",
                    default = 0,
                    array = R.array.bgModes
                )
                numberSeekBar(
                    labelId = R.string.radius,
                    key = "dock:radius",
                    default = 0,
                    max = 40,
                )
                numberSeekBar(
                    labelId = R.string.bottom_padding,
                    key = "dock:bottom_padding",
                    default = 10,
                    max = 30,
                )
                numberSeekBar(
                    labelId = R.string.horizontal_margin,
                    key = "dock:margin_x",
                    default = 16,
                    max = 32,
                )
            }
            labelSettings("dock:labels", false, 0xEEEEEEEE.toInt(), 12)
            card {
                switchTitle(R.string.searchbar, "dock:searchbar:enabled", false)
                color(
                    labelId = R.string.background,
                    iconId = R.drawable.ic_color,
                    key = "dock:searchbar:background_color",
                    default = 0xDDFFFFFF.toInt(),
                )
                color(
                    labelId = R.string.hint_color,
                    iconId = R.drawable.ic_color,
                    key = "dock:searchbar:text_color",
                    default = 0xFF000000.toInt(),
                )
                numberSeekBar(
                    labelId = R.string.radius,
                    key = "dock:searchbar:radius",
                    default = 30,
                    max = 30,
                )
                switch(
                    labelId = R.string.show_below_apps,
                    iconId = R.drawable.ic_arrow_down,
                    key = "dock:searchbar:below_apps",
                    default = true,
                )
            }
        }
        Global.customized = true
    }
}