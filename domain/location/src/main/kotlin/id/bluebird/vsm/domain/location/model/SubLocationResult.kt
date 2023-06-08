package id.bluebird.vsm.domain.location.model

import androidx.annotation.Keep

@Keep
data class SubLocationResult(val id: Long, val name: String, val prefix: String, val haveDeposition : Boolean, val depositionId : Long)
