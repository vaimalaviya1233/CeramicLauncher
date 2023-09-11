package one.zagura.CeramicLauncher.ui.search

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.DragEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.hideKeyboard
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import one.zagura.CeramicLauncher.Home
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.drawable.FastColorDrawable
import one.zagura.CeramicLauncher.data.items.LauncherItem
import one.zagura.CeramicLauncher.ui.ItemLongPress
import one.zagura.CeramicLauncher.provider.search.AppProvider
import one.zagura.CeramicLauncher.provider.search.ContactProvider
import one.zagura.CeramicLauncher.provider.search.Searcher
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager
import one.zagura.CeramicLauncher.util.storage.Settings
import kotlin.math.abs

class SearchActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var searchTxt: EditText

    private var firstResult: LauncherItem? = null

    private val searcher = Searcher(::AppProvider, ::ContactProvider) { query, results ->
        recycler.adapter = SearchAdapter(this, results)
        firstResult = results.firstOrNull()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)
        searchTxt = findViewById(R.id.searchTxt)
        searchTxt.requestFocus()
        recycler = findViewById(R.id.searchgrid)
        val stackFromBottom = Settings["search:start_from_bottom", false]
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, stackFromBottom)
        if (stackFromBottom)
            findViewById<View>(R.id.searchResultsPusher).visibility = View.VISIBLE
        searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searcher.query(this@SearchActivity, s.toString())
            }
        })
        searchTxt.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> onPause()
                EditorInfo.IME_ACTION_GO ->
                    firstResult?.open(this, v, -1)
                        ?: searchOnDuckDuckGo(this, searchTxt.text.toString())
            }
            false
        }
        searchTxt.imeOptions = if (Settings["search:enter_is_go", false]) EditorInfo.IME_ACTION_GO else EditorInfo.IME_ACTION_DONE
        findViewById<View>(R.id.searchbar).background = ShapeDrawable().apply {
            val tr = Settings["search:bar:radius", 0].dp.toFloatPixels(this@SearchActivity)
            shape = RoundRectShape(floatArrayOf(tr, tr, tr, tr, 0f, 0f, 0f, 0f), null, null)
            paint.color = Settings["search:bar:background_color", 0xff242424.toInt()]
        }
        window.setBackgroundDrawable(FastColorDrawable(Settings["search:ui:background_color", -0x78000000]))
        searchTxt.setTextColor(Settings["search:bar:text_color", 0xddffffff.toInt()])
        searchTxt.setHintTextColor(Settings["search:bar:text_color", 0xddffffff.toInt()] and 0xffffff or 0x33000000)
        searchTxt.hint = Settings["search:text", getString(R.string.searchbarhint)]
        with(findViewById<ImageView>(R.id.searchIcon)) {
            imageTintList = ColorStateList.valueOf(Settings["search:bar:text_color", 0xddffffff.toInt()])
        }
        with(findViewById<ImageView>(R.id.kill)) {
            imageTintList = ColorStateList.valueOf(Settings["search:bar:text_color", 0xddffffff.toInt()])
            imageTintMode = PorterDuff.Mode.MULTIPLY
            setOnClickListener {
                startActivity(Intent(this@SearchActivity, Home::class.java))
            }
        }
        window.decorView.findViewById<View>(android.R.id.content).setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val icon = event.localState as View
                    val location = IntArray(2)
                    icon.getLocationOnScreen(location)
                    val y = abs(event.y - location[1])
                    if (y > icon.height / 3.5f) {
                        ItemLongPress.currentPopup?.dismiss()
                        finish()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_STARTED -> {
                    (event.localState as View).visibility = View.INVISIBLE
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    (event.localState as View).visibility = View.VISIBLE
                    ItemLongPress.currentPopup?.isFocusable = true
                    ItemLongPress.currentPopup?.update()
                    true
                }
                else -> false
            }
        }
        searcher.onCreate(this)
    }

    fun searchOnDuckDuckGo(context: Context, string: String) {
        val encoded = Uri.encode(string)
        val url = "https://duckduckgo.com/?q=$encoded&t=one.zagura.CeramicLauncher"
        val uri = Uri.parse(url)
        val i = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(i, ActivityOptions.makeCustomAnimation(context, R.anim.slideup, R.anim.slidedown).toBundle())
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        hideKeyboard()
        finish()
    }

    companion object {
        fun open(context: Context) = context.startActivity(
            Intent(context, SearchActivity::class.java),
            ActivityOptions.makeCustomAnimation(context, R.anim.fadein, R.anim.fadeout).toBundle())
    }
}