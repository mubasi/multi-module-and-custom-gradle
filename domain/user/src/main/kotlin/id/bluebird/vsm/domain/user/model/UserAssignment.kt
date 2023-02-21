package id.bluebird.vsm.domain.user.model


data class UserAssignment(
    val id: Long,
    val locationId: Long = -1,
    val subLocationId: Long,
    val locationName: String,
    val subLocationName: String? = null,
    val isOfficer: Boolean = true
)