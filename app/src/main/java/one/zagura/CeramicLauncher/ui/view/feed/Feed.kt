package one.zagura.CeramicLauncher.ui.view.feed

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.posidon.android.conveniencelib.getStatusBarHeight
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.external.widgets.Widget
import one.zagura.CeramicLauncher.provider.notifications.NotificationService
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Gestures
import one.zagura.CeramicLauncher.ui.view.NestedScrollView
import one.zagura.CeramicLauncher.ui.view.drawer.DrawerView
import one.zagura.CeramicLauncher.ui.view.feed.notifications.NotificationCard
import java.util.*

class Feed : FrameLayout {

    lateinit var drawer: DrawerView

    inline fun init(drawer: DrawerView) {
        this.drawer = drawer
        onTopOverScroll = {
            if (drawer.state != BottomSheetBehavior.STATE_EXPANDED) {
                Gestures.performTrigger(Settings["gesture:top_overscroll", Gestures.PULL_DOWN_NOTIFICATIONS], drawer.context)
            }
        }
        onBottomOverScroll = {
            Gestures.performTrigger(Settings["gesture:bottom_overscroll", Gestures.OPEN_APP_DRAWER], drawer.context)
        }
    }

    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet?) : super(c, a)
    constructor(c: Context, a: AttributeSet?, sa: Int) : super(c, a, sa)

    private val sections = ArrayList<FeedSection>()

    val desktopContent = LinearLayout(context).apply {
        orientation = VERTICAL
        setOnLongClickListener(Gestures::onLongPress)
    }

    val scroll = NestedScrollView(context).apply {
        overScrollMode = OVER_SCROLL_ALWAYS
        isNestedScrollingEnabled = false
        isSmoothScrollingEnabled = false

        setOnLongClickListener(Gestures::onLongPress)
        setOnTouchListener(::onScrollTouch)

        addView(desktopContent, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    private val scaleGestureDetector = ScaleGestureDetector(context, Gestures.PinchListener(context))
    private fun onScrollTouch(v: View?, event: MotionEvent) = if (hasWindowFocus()) {
        scaleGestureDetector.onTouchEvent(event)
        false
    } else true

    var musicCard: MusicCard? = null
        private set

    var notifications: NotificationCard? = null
        private set

    init {
        addView(scroll, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    var onTopOverScroll by scroll::onTopOverScroll
    var onBottomOverScroll by scroll::onBottomOverScroll

    fun clearSections() {
        sections.clear()
        desktopContent.removeAllViews()
        musicCard = null
        notifications = null
    }

    inline fun add(section: FeedSection) {
        getSectionsFromSettings().add(section.toString())
        Settings.apply()
        internalAdd(section)
    }

    inline fun add(section: FeedSection, i: Int) {
        getSectionsFromSettings().add(i, section.toString())
        Settings.apply()
        internalAdd(section, i)
        updateIndices(i + 1)
    }

    fun remove(section: FeedSection) {
        getSectionsFromSettings().remove(section.toString()).let { if (!it) println("Couldn't remove feed section: $section") }
        Settings.apply()
        sections.remove(section)
        desktopContent.removeView(section as View)
        section.onDelete(this)
    }

    fun remove(section: FeedSection, i: Int) {
        getSectionsFromSettings().removeAt(i)
        Settings.apply()
        sections.removeAt(i)
        desktopContent.removeViewAt(i)
        section.onDelete(this)
        updateIndices(i)
    }

    fun internalAdd(section: FeedSection): FeedSection {
        val i = sections.size
        return internalAdd(section, i)
    }

    fun internalAdd(section: FeedSection, i: Int): FeedSection {
        sections.add(section)
        desktopContent.addView(section as View)
        section.onAdd(this, i)
        return section
    }

    private fun updateTheme(activity: Home, drawer: DrawerView) {
        scroll.setOnScrollChangeListener { _, _, y, _, oldY ->
            val a = 6.dp.toFloatPixels(context)
            val distance = oldY - y
            handleDockOnScroll(distance, a, y, drawer)
        }
        val fadingEdge = Settings["feed:fading_edge", true]
        if (fadingEdge && !Settings["hidestatus", false]) {
            scroll.setPadding(0, context.getStatusBarHeight() - 12.dp.toPixels(context), 0, 0)
        }
        scroll.isVerticalFadingEdgeEnabled = fadingEdge

        if (notifications != null || Settings["notif:badges", true]) {
            NotificationService.onUpdate = {
                try {
                    if (notifications != null) activity.runOnUiThread {
                        notifications?.update()
                    }
                    if (Settings["notif:badges", true]) activity.runOnUiThread {
                        drawer.drawerGrid.invalidateViews()
                        drawer.dock.loadApps()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            try { activity.startService(Intent(context, NotificationService::class.java)) }
            catch (e: Exception) {}
        }
    }

    private fun handleDockOnScroll(distance: Int, threshold: Float, y: Int, drawer: DrawerView) {
        if (distance > threshold || y < threshold || y >= desktopContent.height - drawer.dock.dockHeight - height) {
            drawer.isHideable = false
            drawer.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (distance < -threshold) {
            drawer.isHideable = true
            drawer.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    inline fun scrollUpdate(firstScroll: Int, firstMaxScroll: Int) {
        scroll.scrollTo(0, if (Settings["feed:rest_at_bottom", false]) firstScroll + desktopContent.height - firstMaxScroll else firstScroll)
    }

    fun onResume(activity: Activity) {
        for (section in sections) {
            section.onResume(activity)
        }
    }

    fun onPause() {
        for (section in sections) {
            section.onPause()
        }
    }

    fun update(activity: Home, drawer: DrawerView) {
        val map = HashMap<String, FeedSection>()
        for (s in sections) {
            map[s.toString()] = s
        }
        clearSections()
        val s = getSectionsFromSettings()
        var i = 0
        var removeCount = 0
        while (i < s.size) {
            (map[s[i]] ?: FeedSection(activity, s, i))?.let { section ->
                section as View
                if (section.parent == desktopContent) {
                    remove(section, i - removeCount++)
                } else {
                    internalAdd(section)
                    when (section) {
                        is MusicCard -> musicCard = section
                        is NotificationCard -> notifications = section
                    }
                    section.updateTheme(activity)
                }
            }
            i++
        }
        updateTheme(activity, drawer)
    }

    fun updateIndices(fromI: Int) {
        for (i in fromI until sections.size)
            sections[i].updateIndex(i)
    }

    fun onAppsLoaded() {
        val iterator = iterator()
        for (s in iterator) {
            s.onAppsLoaded(iterator)
        }
    }

    operator fun iterator() = object : MutableIterator<FeedSection> {

        private var limit: Int = sections.size

        private var cursor = 0
        private var lastRet = -1 // index of last element returned; -1 if no such

        override fun hasNext(): Boolean {
            return cursor < limit
        }

        override fun next(): FeedSection {
            val i = cursor
            if (i >= limit) throw NoSuchElementException()
            cursor = i + 1
            return sections[i.also { lastRet = it }]
        }

        override fun remove() {
            check(lastRet >= 0)
            try {
                remove(sections[lastRet])
                cursor = lastRet
                lastRet = -1
                limit--
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }
    }

    companion object {

        fun getSectionsFromSettings() = Settings.getStringsOrSet("feed:sections") {
            arrayListOf("music", "notifications")
        }

        fun selectFeedSectionToAdd(activity: Activity, onSelect: (String) -> Unit) {
            val sections = getSectionsFromSettings()
            BottomSheetDialog(activity, R.style.bottomsheet).apply {
                setContentView(R.layout.feed_section_options)
                window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottom_sheet)
                findViewById<View>(R.id.notifications_section)!!.run {
                    if (sections.contains("notifications")) {
                        visibility = GONE
                    } else setOnClickListener {
                        onSelect("notifications")
                        dismiss()
                    }
                }
                findViewById<View>(R.id.starred_contacts_section)!!.run {
                    if (sections.contains("starred_contacts")) {
                        visibility = GONE
                    } else setOnClickListener {
                        onSelect("starred_contacts")
                        dismiss()
                    }
                }
                findViewById<View>(R.id.music_section)!!.run {
                    if (sections.contains("music")) {
                        visibility = GONE
                    } else setOnClickListener {
                        onSelect("music")
                        dismiss()
                    }
                }
                findViewById<View>(R.id.widget_section)!!.setOnClickListener {
                    Widget.selectWidget(activity) {
                        onSelect(it.toString())
                    }
                    dismiss()
                }
                findViewById<View>(R.id.spacer_section)!!.setOnClickListener {
                    onSelect("spacer:96")
                    dismiss()
                }
                show()
            }
        }
    }


    private var oldPointerY = 0f
    private var newPointerY = 0f
    private var timeSincePress = 0L

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                timeSincePress = System.currentTimeMillis()
                oldPointerY = ev.y
                newPointerY = ev.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                oldPointerY = newPointerY
                newPointerY = ev.y
                if (System.currentTimeMillis() - timeSincePress < 240 && ev.pointerCount == 1) {
                    if (oldPointerY < newPointerY) {
                        onTopOverScroll()
                    } else if (oldPointerY > newPointerY) {
                        onBottomOverScroll()
                    }
                }
            }
        }
        return super.onTouchEvent(ev)
    }
}