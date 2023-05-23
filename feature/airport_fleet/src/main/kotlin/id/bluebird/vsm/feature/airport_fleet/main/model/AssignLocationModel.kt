package id.bluebird.vsm.feature.airport_fleet.main.model

import androidx.annotation.Keep

@Keep
data class AssignLocationModel(
    val id: Long = -1,
    val name: String = "-",
    val request: Long = 0,
    var checked: Boolean = false,
    val isWithPassenger: Boolean = false,
    val isNonTerminal: Boolean = false
) {
    companion object {
        fun isNonTerminal(subLocation: String): Boolean =
            (subLocation.toLowerCase() == "with passenger" ||
                    subLocation.toLowerCase() == "without passenger")

    }
}
