package one.zagura.CeramicLauncher.ui.view.feed

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.ui.view.FlagView
import one.zagura.CeramicLauncher.ui.view.ResizableLayout
import one.zagura.CeramicLauncher.util.storage.Settings

class FlagSection(context: Context) : ResizableLayout(context), FeedSection {

    val flag = FlagView(context)

    init {
        addView(flag)
    }

    override fun onAdd(feed: Feed, i: Int) {
        layoutParams.height = Settings["flag:height", 64.dp.toPixels(context)]
        onResizeListener = object : OnResizeListener {
            override fun onStop(newHeight: Int) { Settings["flag:height"] = newHeight }
            override fun onCrossPress() = feed.remove(this@FlagSection)
            override fun onMajorUpdate(newHeight: Int) {}
            override fun onUpdate(newHeight: Int) {
                layoutParams.height = newHeight
                layoutParams = layoutParams
            }
        }
    }

    override fun updateTheme(activity: Activity) {
        flag.colors = Settings.getStringsOrSetEmpty("flag:colors").map { it.toInt(16) }.toIntArray()
    }
}