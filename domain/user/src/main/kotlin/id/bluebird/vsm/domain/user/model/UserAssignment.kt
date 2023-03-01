package id.bluebird.vsm.domain.user.model


data class UserAssignment(
    val id: Long,
    val locationId: Long = -1,
    val subLocationId: Long,
    var locationName: String,
    var subLocationName: String? = null,
    val isOfficer: Boolean = true
)