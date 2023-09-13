package one.zagura.CeramicLauncher.ui.customizations

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.ui.view.setting.*

class CustomNotifications : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.notifications) {
            color(
                R.string.background,
                R.drawable.ic_droplet,
                "notif:background_color",
                0xFFFFFFFF.toInt(),
            )
            color(
                R.string.swipe_bg_color,
                R.drawable.ic_droplet,
                "notif:card_swipe_bg_color",
                0x880d0e0f.toInt(),
            )
            color(
                R.string.title_color,
                R.drawable.ic_droplet,
                "notif:title_color",
                0xFF111213.toInt(),
            )
            color(
                R.string.text_color,
                R.drawable.ic_droplet,
                "notif:text_color",
                0xFF252627.toInt(),
            )
            separator()
            numberSeekBar(
                labelId = R.string.corner_radius,
                key = "notif:radius",
                default = 0,
                max = 30,
            )
            numberSeekBar(
                labelId = R.string.horizontal_margin,
                key = "notif:margin_x",
                default = 0,
                max = 32,
            )
            switchTitle(R.string.notification_badges, "notif:badges", true)
            switch(
                R.string.show_number,
                R.drawable.ic_text,
                "notif:badges:show_num",
                default = true,
            )
            spinner(
                R.string.background_type,
                R.drawable.ic_color_dropper,
                "notif:badges:bg_type",
                0,
                R.array.notifBadgesBGs,
            )
            color(
                R.string.background,
                R.drawable.ic_droplet,
                "notif:badges:bg_color",
                0xffff5555.toInt(),
            )
            clickable(R.string.hidden_apps, R.drawable.ic_visible, ::openHideApps)
        }
        Global.customized = true
    }

    fun openHideApps(v: View) = startActivity(Intent(this, CustomHiddenAppNotifications::class.java))
}