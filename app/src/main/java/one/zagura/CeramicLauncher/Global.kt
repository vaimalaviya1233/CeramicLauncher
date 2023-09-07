package one.zagura.CeramicLauncher

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import one.zagura.CeramicLauncher.items.App
import one.zagura.CeramicLauncher.tools.Tools
import kotlin.math.max
import kotlin.math.min

object Global {

    fun getPastelAccent(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(accentColor, hsv)
        hsv[1] = min(hsv[1],0.5f)
        hsv[2] = min(max(0.4f, hsv[2]), 0.75f)
        return Color.HSVToColor(hsv)
    }

    var appSections = ArrayList<ArrayList<App>>()
    var apps = ArrayList<App>()

    var shouldSetApps = true
    var customized = false

    var accentColor = -0xeeaa01
}