package one.zagura.CeramicLauncher.provider.search

import android.app.Activity
import android.content.Context
import one.zagura.CeramicLauncher.data.items.App
import one.zagura.CeramicLauncher.data.items.LauncherItem

interface SearchProvider {

    fun Activity.onCreate() {}
    fun getResults(context: Context, query: String): List<Pair<LauncherItem, Float>>

    fun onAppsLoaded(context: Context, list: List<App>) {}

    companion object {
        fun matchInitials(query: String, string: String): Boolean {
            val initials = string.split(Regex("([ .\\-_]|([a-z](?=[A-Z0-9])))")).mapNotNull(String::firstOrNull).joinToString("")
            val initialsBasic = string.split(Regex("[ .\\-_]")).mapNotNull(String::firstOrNull).joinToString("")
            return initials.startsWith(query, ignoreCase = true) || initialsBasic.startsWith(query, ignoreCase = true)
        }
    }
}