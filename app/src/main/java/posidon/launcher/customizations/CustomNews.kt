package posidon.launcher.customizations

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import posidon.launcher.Global
import posidon.launcher.R
import posidon.launcher.feed.news.RemovedArticles
import posidon.launcher.feed.news.chooser.FeedChooser
import posidon.launcher.feed.news.opml.OPML
import posidon.launcher.feed.news.opml.OpmlElement
import posidon.launcher.storage.ExternalStorage
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Device
import posidon.launcher.tools.Tools
import posidon.launcher.tools.applyFontSetting
import posidon.launcher.view.Spinner
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomNews : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFontSetting()
        setContentView(R.layout.custom_news)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        findViewById<View>(R.id.settings).setPadding(0, 0, 0, Tools.navbarHeight)

        run {
            val newsCardMaxImageWidthSlider = findViewById<SeekBar>(R.id.newsCardMaxImageWidthSlider)
            val maxWidth = Settings["feed:max_img_width", Device.displayWidth]
            newsCardMaxImageWidthSlider.progress = (maxWidth.toFloat() / Device.displayWidth.toFloat() * 6).toInt() - 1
            newsCardMaxImageWidthSlider.max = 5
            val newsCardMaxImageWidthNum = findViewById<TextView>(R.id.newsCardMaxImageWidthNum)
            newsCardMaxImageWidthNum.text = maxWidth.toString()
            newsCardMaxImageWidthSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) = Settings.apply()
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val newVal: Int = Device.displayWidth / 6 * (progress + 1)
                    newsCardMaxImageWidthNum.text = newVal.toString()
                    Settings["feed:max_img_width"] = newVal
                }
            })
        }

        findViewById<Spinner>(R.id.readMethods).apply {
            data = resources.getStringArray(R.array.articleReadingMethods)
            selectionI = when(Settings["feed:openLinks", "browse"]) {
                "webView" -> 1; "app" -> 2; else -> 0
            }
            setSelectionChangedListener {
                Settings["feed:openLinks"] = when(selectionI) {
                    1 -> "webView"; 2 -> "app"; else -> "browse"
                }
            }
        }

        Global.customized = true
    }

    override fun onPause() {
        Global.customized = true
        super.onPause()
    }

    fun chooseFeeds(v: View) = startActivity(Intent(this, FeedChooser::class.java))
    fun seeRemovedArticles(v: View) = startActivity(Intent(this, RemovedArticles::class.java))

    fun exportOPML(v: View) {
        val feedUrls = ArrayList(Settings["feedUrls", FeedChooser.defaultSources].split("|"))
        if (feedUrls.size == 1 && feedUrls[0].replace(" ", "") == "") {
            feedUrls.removeAt(0)
            Settings.putNotSave("feedUrls", "")
            Settings.apply()
        }

        ExternalStorage.writeOutsideScope(this, "posidon_feed_sources_${SimpleDateFormat("MMdHHmmss", Locale.getDefault()).format(Date())}.opml") { out, path ->
            try {
                OPML.writeDocument(feedUrls.map {
                    OpmlElement(it, it)
                }, out.bufferedWriter(Charsets.UTF_8))
                Toast.makeText(this, "Saved: $path", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun importOPML(v: View) =
        try { ExternalStorage.pickFile(this, "*/*") }
        catch (e: Exception) { e.printStackTrace() }

    private fun doImportOPML(input: InputStream?) {
        val feedUrls = ArrayList(Settings["feedUrls", FeedChooser.defaultSources].split("|"))
        if (feedUrls.size == 1 && feedUrls[0].replace(" ", "") == "") {
            feedUrls.removeAt(0)
            Settings.putNotSave("feedUrls", "")
        }

        try {
            val new = OPML.readDocument(input!!.bufferedReader(Charsets.UTF_8))
            var amountOfNewSources = 0
            for (element in new) {
                if (!feedUrls.contains(element.xmlUrl)) {
                    feedUrls.add(element.xmlUrl)
                    amountOfNewSources++
                }
            }
            Settings.putNotSave("feedUrls", feedUrls.joinToString("|"))
            Toast.makeText(this, "Imported $amountOfNewSources sources", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
        Settings.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            ExternalStorage.onActivityResultPickFile(this, requestCode, data, ::doImportOPML)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}