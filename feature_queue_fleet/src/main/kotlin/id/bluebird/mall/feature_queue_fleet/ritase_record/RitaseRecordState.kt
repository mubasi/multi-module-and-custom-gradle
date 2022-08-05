package id.bluebird.mall.feature_queue_fleet.ritase_record

sealed class RitaseRecordState {
    data class ShowList(val subLocationId: Long): RitaseRecordState()
    data class SuccessSelect(val ticket: String, val subLocationId: Long): RitaseRecordState()
}