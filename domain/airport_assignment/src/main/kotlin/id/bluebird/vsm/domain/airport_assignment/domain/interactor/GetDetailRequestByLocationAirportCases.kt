package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetDetailRequestInLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetDetailRequestByLocationAirport
import id.bluebird.vsm.domain.airport_assignment.model.GetDetailRequestInLocationAirportModel
import id.bluebird.vsm.domain.airport_assignment.model.SubLocationItemAirportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetDetailRequestByLocationAirportCases(
    private val airportAssignment: AirportAssignmentRepository
) : GetDetailRequestByLocationAirport {
    override fun invoke(
        locationId: Long,
        showWingsChild: Boolean
    ): Flow<GetDetailRequestInLocationAirportState> = flow {
        val response = airportAssignment.getDetailRequestInLocation(
            locationId, showWingsChild
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val subLocationItem : ArrayList<SubLocationItemAirportModel> = ArrayList()

        response.subLocationItemsList.forEach {
            subLocationItem.add(
                SubLocationItemAirportModel(
                    subLocationId = it.subLocationId,
                    subLocationName = it.subLocationName,
                    count = it.count,
                )
            )
        }

        val result = GetDetailRequestInLocationAirportModel(
            subLocationItem = subLocationItem,
        )

        emit(GetDetailRequestInLocationAirportState.Success(result))

    }
}