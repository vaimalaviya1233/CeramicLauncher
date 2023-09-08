package one.zagura.CeramicLauncher.ui.customizations.settingScreens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.Gestures
import one.zagura.CeramicLauncher.ui.view.setting.*

class CustomGestures : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureWindowForSettings()
        setSettingsContentView(R.string.settings_title_gestures) {
            actionSelector(
                labelId = R.string.pinch,
                iconId = R.drawable.ic_apps,
                key = "gesture:pinch",
                default = Gestures.OPEN_OVERVIEW,
            )
            actionSelector(
                labelId = R.string.long_press,
                iconId = R.drawable.ic_apps,
                key = "gesture:long_press",
                default = Gestures.OPEN_OVERVIEW,
            )
            actionSelector(
                labelId = R.string.back_button,
                iconId = R.drawable.ic_arrow_left,
                key = "gesture:back",
                default = Gestures.NOTHING,
            )
            actionSelector(
                labelId = R.string.gesture_top_overscroll,
                iconId = R.drawable.ic_arrow_up,
                key = "gesture:top_overscroll",
                default = Gestures.PULL_DOWN_NOTIFICATIONS,
            )
            actionSelector(
                labelId = R.string.gesture_bottom_overscroll,
                iconId = R.drawable.ic_arrow_down,
                key = "gesture:bottom_overscroll",
                default = Gestures.OPEN_APP_DRAWER,
            )
        }
    }
}