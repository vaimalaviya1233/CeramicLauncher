package one.zagura.CeramicLauncher.ui.customizations

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.ui.customizations.order.FeedOrderActivity
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.setting.*

class CustomHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_feed) {
            numberSeekBar(
                labelId = R.string.vertical_margin,
                key = "feed:card_margin_y",
                default = 9,
                max = 16,
            )
            switch(
                labelId = R.string.show_behind_dock,
                iconId = R.drawable.ic_visible,
                key = "feed:show_behind_dock",
                default = false,
            )
            switch(
                labelId = R.string.keep_position,
                iconId = R.drawable.ic_other,
                key = "feed:keep_pos",
                default = false,
            )
            switch(
                labelId = R.string.rest_at_bottom,
                iconId = R.drawable.ic_arrow_down,
                key = "feed:rest_at_bottom",
                default = false,
            )
            switch(
                labelId = R.string.fading_edge,
                iconId = R.drawable.ic_visible,
                key = "feed:fading_edge",
                default = true,
            )
        }
        Global.customized = true
    }

    override fun onPause() {
        Global.customized = true
        super.onPause()
    }

    fun openFeedOrder(v: View) = startActivity(Intent(this, FeedOrderActivity::class.java))
}