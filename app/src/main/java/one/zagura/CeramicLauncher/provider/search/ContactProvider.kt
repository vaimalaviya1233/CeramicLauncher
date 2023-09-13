package one.zagura.CeramicLauncher.provider.search

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.willowtreeapps.fuzzywuzzy.diffutils.FuzzySearch
import one.zagura.CeramicLauncher.data.items.ContactItem
import one.zagura.CeramicLauncher.util.storage.Settings
import kotlin.collections.ArrayList

class ContactProvider(
    searcher: Searcher
) : SearchProvider {

    private var contacts = emptyList<ContactItem>()
    private var canReadContacts = true

    override fun Activity.onCreate() {
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 0)
            canReadContacts = false
            return
        }
        contacts = ContactItem.getList(this, false).toList()
    }

    override fun getResults(context: Context, query: String): List<Pair<ContactItem, Float>> {
        val results = ArrayList<Pair<ContactItem, Float>>()
        if (canReadContacts && Settings["search:use_contacts", true]) {
            contacts.forEach {
                val name = FuzzySearch.tokenSortPartialRatio(query, it.label) / 100f *
                    if (it.isFavorite) 1.1f else 1f
                val initials = if (
                    query.length > 1 &&
                    SearchProvider.matchInitials(query, it.label)
                ) 0.5f else 0f
                val r = name + initials
                if (r > .6f) {
                    results += it to r
                }
            }
        }
        return results
    }
}