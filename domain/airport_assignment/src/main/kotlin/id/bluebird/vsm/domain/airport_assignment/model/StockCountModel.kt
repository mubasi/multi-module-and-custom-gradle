package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class StockCountModel(
    val stock : Long,
    val request : Long,
    val ritase : Long
)