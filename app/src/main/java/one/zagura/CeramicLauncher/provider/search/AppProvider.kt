package one.zagura.CeramicLauncher.provider.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import com.willowtreeapps.fuzzywuzzy.diffutils.FuzzySearch
import one.zagura.CeramicLauncher.Global
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.data.items.LauncherItem
import one.zagura.CeramicLauncher.data.items.StaticShortcut
import one.zagura.CeramicLauncher.ui.hidden.HiddenAppsActivity
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.util.storage.Settings
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.pow

class AppProvider(
    private val searcher: Searcher
) : SearchProvider {

    private var shortcuts = emptyList<StaticShortcut>()

    private lateinit var launcherApps: LauncherApps

    override fun Activity.onCreate() {
        launcherApps = getSystemService(LauncherApps::class.java)
        onAppsLoaded(this, Global.apps)
    }

    override fun onAppsLoaded(context: Context, list: List<App>) {
        if (Settings["search:use_shortcuts", true]) {
            thread(isDaemon = true) {
                shortcuts = Global.apps.flatMap { app ->
                    app.getShortcuts(context)
                }
            }
        }
    }

    override fun getResults(context: Context, query: String): List<Pair<LauncherItem, Float>> {
        shortcuts = Global.apps.flatMap { app ->
            app.getShortcuts(context)
        }
        val results = ArrayList<Pair<LauncherItem, Float>>()
        //val suggestions = SuggestionsManager.get().let { it.subList(0, it.size.coerceAtMost(6)) }
        val apps = if (Settings["search:include_hidden_apps", false]) {
            Global.apps + App.hidden
        } else Global.apps
        apps.forEach {
//            val i = suggestions.indexOf(it)
//            val suggestionFactor = if(i == -1) 0f else (suggestions.size - i).toFloat() / suggestions.size
            val packageSearch = Settings["search:use_package_names", false]
            val packageFactor = run {
                val r = FuzzySearch.tokenSortPartialRatio(query, it.packageName) / 100f
                r * r * r * 0.8f
            } * if (packageSearch) 0.8f else 0.1f
            val initialsFactor = if (query.length > 1 && SearchProvider.matchInitials(
                    query,
                    it.label
                )
            ) 0.6f else 0f
            val r = FuzzySearch.tokenSortPartialRatio(query, it.label) / 100f +
                //suggestionFactor +
                initialsFactor +
                packageFactor
            if (r > .8f) {
                results += it to r.coerceAtLeast(0.98f)
            }
        }
        if (Settings["search:use_shortcuts", true]) {
            shortcuts.forEach {
                val l = FuzzySearch.tokenSortPartialRatio(query, it.label) / 100f
                val a = FuzzySearch.tokenSortPartialRatio(query, it.app.label) / 100f
                val initials = if (
                    query.length > 1 &&
                    SearchProvider.matchInitials(query, it.label)
                ) 0.5f else 0f
                val r = (a * a * .5f + l * l).pow(.2f) + initials
                if (r > .95f) {
                    results += it to l
                }
            }
        }
        if (query == "hidden" || query == "hiddenapps") {
            val app = LauncherItem.make("Hidden apps", context.getDrawable(R.drawable.hidden_apps)) { context, _, _ ->
                context.startActivity(Intent(context, HiddenAppsActivity::class.java))
            }
            results.add(app to 1f)
        }
        return results
    }
}