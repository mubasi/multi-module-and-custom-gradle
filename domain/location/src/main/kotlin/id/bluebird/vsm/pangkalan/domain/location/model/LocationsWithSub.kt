package id.bluebird.vsm.domain.location.model

data class LocationsWithSub(
    val locationId: Long,
    val locationName: String,
    var list: MutableList<SubLocationResult> = ArrayList(),
)