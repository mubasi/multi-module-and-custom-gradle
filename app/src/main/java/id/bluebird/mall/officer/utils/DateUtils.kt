package id.bluebird.mall.officer.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        fun getTodayDate(): String {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return sdf.format(Date().time)
        }
        fun getLastSycnFormat():String{
            val sdf = SimpleDateFormat("dd MMM yyyy '•' hh:mm", Locale.getDefault())
            return sdf.format(Date().time)
        }
    }
}