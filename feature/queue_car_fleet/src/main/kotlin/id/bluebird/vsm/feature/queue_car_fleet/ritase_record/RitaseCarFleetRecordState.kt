package id.bluebird.vsm.feature.queue_car_fleet.ritase_record

sealed class RitaseCarFleetRecordState {
    data class ShowList(val subLocationId: Long): RitaseCarFleetRecordState()
    data class SuccessSelect(val ticket: String, val subLocationId: Long): RitaseCarFleetRecordState()
}