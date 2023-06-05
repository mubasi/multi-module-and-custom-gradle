package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class AddStockDepartModel(
    val massage : String,
    val stockId : Long,
    val stockType : String,
    val createdAt : String,
    val currentTuSpace : Long,
    val taxiList : List<String>,
    val arrivedItem : List<ArrivedItemModel>
)