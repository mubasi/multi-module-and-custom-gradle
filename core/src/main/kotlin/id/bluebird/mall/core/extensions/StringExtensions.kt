package id.bluebird.mall.core.extensions

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
}