package one.zagura.CeramicLauncher.provider.search

import android.app.Activity
import android.content.Context
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.data.items.LauncherItem
import kotlin.collections.ArrayList

class Searcher(
    vararg providers: (Searcher) -> SearchProvider,
    val update: (String, List<LauncherItem>) -> Unit
) {
    val providers = providers.map { it(this) }

    fun query(context: Context, query: String) {
        val r = ArrayList<Pair<LauncherItem, Float>>()
        providers.flatMapTo(r) { it.getResults(context, query) }
        r.sortWith { a, b ->
            b.second.compareTo(a.second)
        }
        val tr = if (r.size > MAX_RESULTS) r.subList(0, MAX_RESULTS) else r
        update(query, tr.map { it.first })
    }

    fun onCreate(activity: Activity) {
        providers.forEach {
            it.run { activity.onCreate() }
        }
    }

    fun onAppsLoaded(context: Context, list: List<App>) {
        providers.forEach {
            it.onAppsLoaded(context, list)
        }
    }

    companion object {
        const val MAX_RESULTS = 32
    }
}