package one.zagura.CeramicLauncher.customizations.settingScreens

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.storage.Settings
import one.zagura.CeramicLauncher.view.setting.*

class CustomSearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_search) {
            card {
                custom {
                    layoutInflater.inflate(R.layout.custom_search_hint_entry, viewGroup, false)
                }
                color(
                    labelId = R.string.text_color,
                    iconId = R.drawable.ic_color,
                    key = "searchtxtcolor",
                    default = 0xFFFFFFFF.toInt(),
                )
                color(
                    labelId = R.string.background,
                    iconId = R.drawable.ic_color,
                    key = "searchUiBg",
                    default = 0x88000000.toInt(),
                )
                numberSeekBar(
                    labelId = R.string.iconSize,
                    key = "search:icons:size",
                    default = 56,
                    max = 96,
                    startsWith1 = true,
                )
                switch(
                    labelId = R.string.stack_results_from_bottom,
                    iconId = R.drawable.ic_arrow_down,
                    key = "search:start_from_bottom",
                    default = false,
                )
                switch(
                    labelId = R.string.search_as_home,
                    iconId = R.drawable.ic_home,
                    key = "search:asHome",
                    default = false,
                )
                switch(
                    labelId = R.string.enter_is_go,
                    iconId = R.drawable.ic_arrow_right,
                    key = "search:enter_is_go",
                    default = false,
                )
            }
            card {
                title(R.string.results)
                switch(
                    labelId = R.string.package_search,
                    iconId = R.drawable.ic_label,
                    key = "search:use_package_names",
                    default = false,
                )
                switch(
                    labelId = R.string.shortcuts,
                    iconId = R.drawable.ic_apps,
                    key = "search:use_shortcuts",
                    default = true,
                )
                switch(
                    labelId = R.string.contacts,
                    iconId = R.drawable.ic_apps,
                    key = "search:use_contacts",
                    default = true,
                )
                switch(
                    labelId = R.string.include_hidden_apps,
                    iconId = R.drawable.ic_visible,
                    key = "search:include_hidden_apps",
                    default = false,
                )
                switch(
                    labelId = R.string.duckduckgo_results,
                    iconId = R.drawable.ic_search,
                    key = "search:ddg_instant_answers",
                    default = true,
                )
            }
        }
        TextViewCompat.setCompoundDrawableTintList(findViewById(R.id.hint_label), ColorStateList.valueOf(Global.getPastelAccent()))
        findViewById<TextView>(R.id.hinttxt).text = Settings["searchhinttxt", getString(R.string.searchbarhint)]
        Global.customized = true
    }

    override fun onPause() {
        Settings["searchhinttxt"] = findViewById<TextView>(R.id.hinttxt).text.toString()
        super.onPause()
    }
}