package one.zagura.CeramicLauncher.data.items

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import io.posidon.android.conveniencelib.drawable.MaskedDrawable
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.ui.drawable.NonDrawable
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Tools
import one.zagura.CeramicLauncher.util.theme.Icons
import java.io.FileNotFoundException


class ContactItem private constructor(
    override var label: String,
    override val icon: Drawable,
    val lookupKey: String,
    val phone: String?,
) : LauncherItem() {

    override fun open(context: Context, view: View, dockI: Int) {
        val viewContact = Intent(Intent.ACTION_VIEW)
        viewContact.data = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
        viewContact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        viewContact.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        context.startActivity(viewContact)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactItem

        if (lookupKey != other.lookupKey) return false
        if (phone != other.phone) return false
        if (label != other.label) return false

        return true
    }

    override fun hashCode() = lookupKey.hashCode()

    companion object {
        fun getList(requiresStar: Boolean = false): Iterable<ContactItem> {
            val cur = Tools.appContext!!.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.LOOKUP_KEY,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.STARRED,
                        ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
                        ContactsContract.Contacts.PHOTO_ID), null, null, null)

            val contactMap = HashMap<String, ContactItem>()

            if (cur != null) {

                val lookupIndex = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)
                val displayNameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val starredIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED)
                val photoIdIndex = cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)

                if (cur.count != 0) {

                    val tmpLAB = DoubleArray(3)
                    val textP = Paint().apply {
                        color = 0xffffffff.toInt()
                        typeface = Tools.appContext!!.resources.getFont(R.font.rubik_medium_caps)
                        textAlign = Paint.Align.CENTER
                        textSize = 64f
                        isAntiAlias = true
                        isSubpixelText = true
                    }

                    while (cur.moveToNext()) {
                        val starred = cur.getInt(starredIndex) != 0
                        if (requiresStar && !starred) {
                            continue
                        }
                        val lookupKey = cur.getString(lookupIndex)
                        val name = cur.getString(displayNameIndex)
                        if (name.isNullOrBlank()) continue
                        val phone = cur.getString(numberIndex)
                        val photoId = cur.getString(photoIdIndex)
                        val iconUri: Uri? = if (photoId != null) {
                            ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId.toLong())
                        } else null

                        val pic = if (iconUri == null) Icons.generateContactPicture(name, tmpLAB, textP) ?: NonDrawable() else try {
                            val inputStream = Tools.appContext!!.contentResolver.openInputStream(iconUri)
                            Drawable.createFromStream(inputStream, iconUri.toString())
                        } catch (e: FileNotFoundException) { Icons.generateContactPicture(name, tmpLAB, textP) } ?: NonDrawable()
                        pic.setBounds(0, 0, pic.intrinsicWidth, pic.intrinsicHeight)

                        val icon = MaskedDrawable(pic, Path().apply {
                            addCircle(pic.intrinsicWidth / 2f, pic.intrinsicHeight / 2f, pic.intrinsicWidth / 2f - 2, Path.Direction.CCW)
                            fillType = Path.FillType.INVERSE_EVEN_ODD
                        })

                        val contact = ContactItem(name, icon, lookupKey, phone)

                        if (!contactMap.containsKey(lookupKey)) {
                            contactMap[lookupKey] = contact
                        }
                    }
                }
                cur.close()
            }

            val nicknameCur = Tools.appContext!!.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Nickname.NAME, ContactsContract.Data.LOOKUP_KEY),
                ContactsContract.Data.MIMETYPE + "= ?",
                arrayOf(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE), null)

            if (nicknameCur != null) {
                if (nicknameCur.count != 0) {
                    val lookupKeyIndex = nicknameCur.getColumnIndex(ContactsContract.Data.LOOKUP_KEY)
                    val nickNameIndex = nicknameCur.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME)
                    while (nicknameCur.moveToNext()) {
                        val lookupKey = nicknameCur.getString(lookupKeyIndex)
                        val nickname = nicknameCur.getString(nickNameIndex)
                        if (nickname != null && lookupKey != null && contactMap.containsKey(lookupKey)) {
                            contactMap[lookupKey]!!.label = nickname
                        }
                    }
                }
                nicknameCur.close()
            }

            return contactMap.values
        }
    }

    override fun toString() = lookupKey
}