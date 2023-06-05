package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class FleetItemDepartModel(
    val fleetId: Long = -1,
    val taxiNo: String = "",
    val createdAt: String = "",
    val status: String = EStatus.ARRIVED.name,
    val isTu: Boolean = false,
    val sequence: Long = -1,
    val isSelected : Boolean = false
)

enum class EStatus {
    ARRIVED, OTW
}