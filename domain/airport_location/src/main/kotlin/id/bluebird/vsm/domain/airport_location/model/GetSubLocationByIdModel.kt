package id.bluebird.vsm.domain.airport_location.model

import androidx.annotation.Keep

@Keep
data class GetSubLocationByIdModel(
    val locationId : Long,
    val locationName : String,
    val codeArea : String,
    val subLocationList : List<SubLocationItemModel>
)
