package id.bluebird.vsm.domain.user.model

data class AssignmentLocationItem(
    val subLocationId: Long = -1,
    val subLocationName: String = "",
    val isDeposition: Boolean = false,
    val locationId: Long = -1,
    val locationName: String = "",
    val isWings: Boolean = false,
    val prefix: String = ""
)
