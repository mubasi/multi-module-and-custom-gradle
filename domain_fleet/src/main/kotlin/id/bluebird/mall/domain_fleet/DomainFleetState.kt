package id.bluebird.mall.domain_fleet

import id.bluebird.mall.domain_fleet.model.CountResult
import id.bluebird.mall.domain_fleet.model.FleetItemResult

sealed class GetCountState {
    data class Success(val countResult: CountResult) : GetCountState()
}

sealed class AddFleetState {
    data class Success(val fleetItemResult: FleetItemResult) : AddFleetState()
}

sealed class SearchFleetState {
    data class Success(val fleetNumbers: List<String>) : SearchFleetState()
    object EmptyResult : SearchFleetState()
}

sealed class RequestState {
    data class Success(val count: Long) : RequestState()
    object CountInvalid : RequestState()
    object SubLocationInvalid : RequestState()
}

sealed class GetListFleetState {
    data class Success(val list: List<FleetItemResult>) : GetListFleetState()
    object EmptyResult : GetListFleetState()
}