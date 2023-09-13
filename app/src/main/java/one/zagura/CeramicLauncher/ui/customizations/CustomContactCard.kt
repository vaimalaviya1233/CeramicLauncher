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

class CustomContactCard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        configureWindowForSettings()
        setSettingsContentView(R.string.starred_contacts) {
            numberSeekBar(
                labelId = R.string.horizontal_margin,
                key = "contacts_card:margin_x",
                default = 16,
                max = 32,
            )
            numberSeekBar(
                labelId = R.string.columns,
                key = "contacts_card:columns",
                default = 5,
                max = 5,
                startsWith1 = true,
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