package posidon.launcher.view.feed

import android.app.Activity
import posidon.android.conveniencelib.dp
import posidon.launcher.storage.Settings
import posidon.launcher.view.ResizableLayout

class SpacerSection(context: Activity) : ResizableLayout(context), FeedSection {

    private var i = -1

    override fun onAdd(feed: Feed, index: Int) {
        this.i = index
        layoutParams.height = context.dp(Feed.getSectionsFromSettings()[i].substringAfter(':').toInt()).toInt()
        onResizeListener = object : OnResizeListener {
            override fun onCrossPress() = feed.remove(this@SpacerSection, i)
            override fun onMajorUpdate(newHeight: Int) {}
            override fun onUpdate(newHeight: Int) {}
            override fun onStop(newHeight: Int) {
                Feed.getSectionsFromSettings()[i] = toString()
                Settings.apply()
            }
        }
    }

    override fun updateIndex(i: Int) {
        this.i = i
    }

    override fun updateTheme(activity: Activity) {}

    override fun toString() = "spacer:" + (layoutParams.height / context.dp(1f)).toInt()
}