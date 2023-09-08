package one.zagura.CeramicLauncher.ui.customizations.settingScreens

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import org.json.JSONArray
import posidon.android.loader.text.TextLoader
import one.zagura.CeramicLauncher.BuildConfig
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.ui.view.recycler.LinearLayoutManager
import java.net.URL

class About : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(false)
        else window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        findViewById<View>(R.id.settings).setPadding(0, 0, 0, Tools.navbarHeight)
        val description = findViewById<TextView>(R.id.appname)
        description.text = getString(R.string.app_name) + " - " + BuildConfig.VERSION_NAME
        findViewById<View>(R.id.icon).setOnLongClickListener {
            if (Settings["dev:enabled", false]) {
                Settings["dev:enabled"] = false
                Toast.makeText(this@About, "Developer mode disabled", Toast.LENGTH_SHORT).show()
            } else {
                Settings["dev:enabled"] = true
                Toast.makeText(this@About, "Developer mode enabled", Toast.LENGTH_SHORT).show()
            }
            true
        }
        val contributorList = findViewById<RecyclerView>(R.id.contributorList).apply {
            layoutManager = LinearLayoutManager(this@About)
            isNestedScrollingEnabled = false
        }
        TextLoader.load("https://api.github.com/repos/zaguragit/CeramicLauncher/contributors") {
            val array = JSONArray(it)
            val contributors = ArrayList<Contributor>()
            for (i in 0 until array.length()) {
                val c = array.getJSONObject(i)
                val name = c.getString("login")
                contributors.add(Contributor(
                    name,
                    BitmapFactory.decodeStream(URL(c.getString("avatar_url")).openStream()),
                    c.getString("html_url")
                ))
            }
            runOnUiThread {
                contributorList.adapter = ListAdapter(this, contributors)
                findViewById<View>(R.id.title).visibility = View.VISIBLE
            }
        }
    }

    class Contributor(
        val name: String,
        val icon: Bitmap,
        val url: String
    )

    class ListAdapter(
        private val context: Context,
        private val contributors: ArrayList<Contributor>
    ) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        class ViewHolder(
            val view: View,
            var icon: ImageView,
            var text: TextView
        ) : RecyclerView.ViewHolder(view)

        override fun getItemCount() = contributors.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            return ViewHolder(v,
                v.findViewById<ImageView>(R.id.iconimg).apply {
                    val p = 8.dp.toPixels(context)
                    setPadding(p, p, p, p)
                },
                v.findViewById(R.id.icontxt)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, i: Int) {

            val contributor = contributors[i]
            holder.icon.setImageBitmap(contributor.icon)
            holder.text.text = contributor.name

            holder.view.setOnClickListener {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(contributor.url)),
                    ActivityOptions.makeCustomAnimation(context, R.anim.slideup, R.anim.slidedown).toBundle()
                )
            }
        }
    }
}