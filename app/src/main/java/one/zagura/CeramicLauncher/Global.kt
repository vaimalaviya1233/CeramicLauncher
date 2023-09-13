package one.zagura.CeramicLauncher

import android.graphics.Color
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.util.theme.ColorTools
import kotlin.math.max
import kotlin.math.min

object Global {

    fun getPastelAccent(): Int =
        ColorTools.pastelizeColor(accentColor)

    var appSections = ArrayList<ArrayList<App>>()
    var apps = ArrayList<App>()

    var shouldSetApps = true
    var customized = false

    var accentColor = -0xeeaa01
}