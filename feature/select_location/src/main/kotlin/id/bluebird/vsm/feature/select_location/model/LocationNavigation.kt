package id.bluebird.vsm.feature.select_location.model

data class LocationNavigation(
    val locationId: Long? = null,
    val subLocationId: Long? = null,
    val locationName: String = "",
    val subLocationName: String = "",
    val prefix: String = ""
) {

}

