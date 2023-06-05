package id.bluebird.vsm.feature.airport_fleet.request_list

sealed class RequestListState {
    object Idle : RequestListState()
    object Progress : RequestListState()
    data class EmptyList(
        val message : Throwable
    ) : RequestListState()
    data class Success(val data: List<FleetRequestDetail>): RequestListState()
}