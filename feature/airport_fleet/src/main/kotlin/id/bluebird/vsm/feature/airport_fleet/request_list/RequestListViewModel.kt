package id.bluebird.vsm.feature.airport_fleet.request_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.GetDetailRequestInLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetDetailRequestByLocationAirport
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RequestListViewModel(
    private val getDetailRequestInLocation: GetDetailRequestByLocationAirport,
) : ViewModel() {

    private val _state: MutableSharedFlow<RequestListState> = MutableSharedFlow()
    val state = _state.asSharedFlow()

    fun init(isWing: Boolean) {
        viewModelScope.launch {
            _state.emit(RequestListState.Progress)
            getDetailAssignment(isWing)
        }
    }

    private fun getDetailAssignment(isWing: Boolean) {
        viewModelScope.launch {
            getDetailRequestInLocation.invoke(
                locationId = UserUtils.getLocationId(),
                showWingsChild = isWing
            )
                .catch { cause ->
                    _state.emit(
                        RequestListState.EmptyList(
                            cause
                        )
                    )
                }
                .collect {
                    when(it) {
                        is GetDetailRequestInLocationAirportState.Success -> {
                            val list = it.result.subLocationItem.map { item ->
                                FleetRequestDetail(
                                    subLocationName = item.subLocationName,
                                    requestCount = item.count.toInt()
                                )
                            }
                            _state.emit(
                                RequestListState.Success(
                                    list.sortedByDescending { row -> row.requestCount }
                                )
                            )
                        }
                    }
                }
        }
    }


}