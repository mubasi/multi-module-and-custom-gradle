package id.bluebird.vsm.core.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.api.client.util.DateTime
import java.text.SimpleDateFormat
import java.util.*

object StringExtensions {

    fun String.getTodayDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date().time)
    }

    fun String.getLastSync(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return "$this, ${sdf.format(Date().time)}"
    }

    fun String.convertCreateAtValue(): String {
        val dateTime = DateTime.parseRfc3339(this)
        val sdf = SimpleDateFormat("dd MMM yyyy '.' HH:mm", Locale("id", "ID"))
        return sdf.format(dateTime.value)
    }

    fun String.convertBase64(): Bitmap {
        val bytes: ByteArray = Base64.decode(this, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}