package id.bluebird.mall.core.utils

import com.google.api.client.util.DateTime
import java.util.*

object DateUtils {
    fun getDateRfc399(): String {
        val dateTime = DateTime(false, Date().time, null)
        return dateTime.toStringRfc3339()
    }
}