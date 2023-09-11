package one.zagura.CeramicLauncher.ui.customizations

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.setting.*

class CustomSearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_search) {
            custom {
                layoutInflater.inflate(R.layout.custom_search_hint_entry, viewGroup, false)
            }
            color(
                labelId = R.string.text_color,
                iconId = R.drawable.ic_droplet,
                key = "search:ui:text_color",
                default = 0xFFFFFFFF.toInt(),
            )
            color(
                labelId = R.string.background,
                iconId = R.drawable.ic_droplet,
                key = "search:ui:background_color",
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
                labelId = R.string.enter_is_go,
                iconId = R.drawable.ic_arrow_right,
                key = "search:enter_is_go",
                default = false,
            )
            title(R.string.searchbar)
            color(
                labelId = R.string.text_color,
                iconId = R.drawable.ic_droplet,
                key = "search:bar:text_color",
                default = 0xFFFFFFFF.toInt(),
            )
            color(
                labelId = R.string.background,
                iconId = R.drawable.ic_droplet,
                key = "search:bar:background_color",
                default = 0x88000000.toInt(),
            )
            numberSeekBar(
                labelId = R.string.background,
                key = "search:bar:radius",
                default = 0,
                max = 30,
            )
            title(R.string.results)
            switch(
                labelId = R.string.package_search,
                iconId = R.drawable.ic_text,
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
                iconId = R.drawable.ic_contact,
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
        TextViewCompat.setCompoundDrawableTintList(findViewById(R.id.hint_label), ColorStateList.valueOf(Global.getPastelAccent()))
        findViewById<TextView>(R.id.hinttxt).text = Settings["search:text", getString(R.string.searchbarhint)]
        Global.customized = true
    }

    override fun onPause() {
        Settings["search:text"] = findViewById<TextView>(R.id.hinttxt).text.toString()
        super.onPause()
    }
}