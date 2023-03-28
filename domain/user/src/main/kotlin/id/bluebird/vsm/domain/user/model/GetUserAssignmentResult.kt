package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep

@Keep
data class GetUserAssignmentResult(
    val assignmentLocationItems: List<AssignmentLocationItem> = ArrayList(),
    val areaCode: String = "",
    val isAirport: Boolean = false
)
