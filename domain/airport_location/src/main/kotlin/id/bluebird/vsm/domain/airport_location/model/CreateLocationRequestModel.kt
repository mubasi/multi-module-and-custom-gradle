package id.bluebird.vsm.domain.airport_location.model

import androidx.annotation.Keep

@Keep
data class CreateLocationRequestModel(
    val id : Long,
    val locationName : String,
    val isActive : Int,
    val createdAt : String,
    val modifiedAt : String,
    val createdBy : Long,
    val modifiedBy : Long,
    val codeArea : String,
    val intervalReset : Long
)