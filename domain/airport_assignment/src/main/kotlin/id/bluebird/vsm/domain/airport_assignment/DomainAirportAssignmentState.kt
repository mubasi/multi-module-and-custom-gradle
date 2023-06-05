package id.bluebird.vsm.domain.airport_assignment

import id.bluebird.vsm.domain.airport_assignment.model.*

sealed class StockDepartState {
    data class Success(
        val result : AddStockDepartModel
    ) : StockDepartState()
}

sealed class RequestTaxiDepartState {
    data class Success(
        val result : RequestTaxiModel
    ) : RequestTaxiDepartState()
    object CountInvalid : RequestTaxiDepartState()
    object SubLocationInvalid : RequestTaxiDepartState()
}

sealed class GetSubLocationStockCountDepartState {
    data class Success(
        val result : StockCountModel
    ) : GetSubLocationStockCountDepartState()
}

sealed class GetListFleetTerminalDepartState {
    data class Success(
        val result : List<FleetItemDepartModel>
    ) : GetListFleetTerminalDepartState()
    object EmptyResult : GetListFleetTerminalDepartState()
}

sealed class SearchStockDepartState {
    data class Success(
        val arrivedList : List<String>
    ) : SearchStockDepartState()
    object EmptyResult : SearchStockDepartState()
}

sealed class GetStatusDriverDepartState {
    data class Success(
        val result : GetStatusDriverDepartModel
    ) : GetStatusDriverDepartState()
}


sealed class GetRequestDepartSubLocationState {
    data class Success(
        val result : List<GetRequestDepartSubLocationModel>
    ) : GetRequestDepartSubLocationState()
}

sealed class DispatchFleetAirportState {
    data class SuccessArrived(val mapStockIds: HashMap<String, Long>) : DispatchFleetAirportState()
    data class SuccessDispatchFleet(val totalDispatch: Int) : DispatchFleetAirportState()
    object WrongDispatchLocation : DispatchFleetAirportState()
}

sealed class GetSubLocationAirportState {
    data class Success(val result: GetSubLocationAirportModel) :
        GetSubLocationAirportState()
}

sealed class RitaseFleetTerminalAirportState{
    data class Success(
        val result : AddStockDepartModel
    ) : RitaseFleetTerminalAirportState()
}

sealed class AssignFleetTerminalAirportState{
    data class Success(
        val result : AssignFleetTerminalAirportModel
    ) : AssignFleetTerminalAirportState()
}

sealed class GetDetailRequestInLocationAirportState{
    data class Success(
        val result : GetDetailRequestInLocationAirportModel
    ) : GetDetailRequestInLocationAirportState()
}