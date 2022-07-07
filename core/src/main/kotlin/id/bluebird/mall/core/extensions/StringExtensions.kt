package id.bluebird.mall.core.extensions

import java.text.SimpleDateFormat
import java.util.*

object StringExtensions {

    fun String.getTodayDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date().time)
    }

    fun String.getLastSync(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy '•' hh:mm", Locale.getDefault())
        return sdf.format(Date().time)
    }
}