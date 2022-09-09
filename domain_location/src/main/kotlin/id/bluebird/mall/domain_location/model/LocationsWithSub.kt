package id.bluebird.mall.domain_location.model

data class LocationsWithSub(
    val locationId: Long,
    val locationName: String,
    var list: MutableList<SubLocationResult> = ArrayList()
)