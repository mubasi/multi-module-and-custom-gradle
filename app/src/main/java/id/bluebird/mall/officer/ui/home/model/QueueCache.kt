package id.bluebird.mall.officer.ui.home.model

import androidx.annotation.Keep

@Keep
data class QueueCache(
    val number: Long = -1,
    val subLocation: String = "A",
    var isCurrentQueue: Boolean = false,
    var isDelay: Boolean = false,
    var isVisible: Boolean = true
) {
    fun getQueue(): String = "${subLocation}.${
        when {
            number < 10 -> {
                "000${number}"
            }
            number < 100 -> {
                "00$number"
            }
            number < 1000 -> {
                "0$number"
            }
            else -> number
        }
    }"
}
