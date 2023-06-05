package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationAirport
import id.bluebird.vsm.domain.airport_assignment.model.CountSubLocationItem
import id.bluebird.vsm.domain.airport_assignment.model.GetSubLocationAirportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetSubLocationAirportCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : GetSubLocationAirport {
    override fun invoke(
        locationId: Long,
        showWingsChild: Boolean,
        versionCode: Long
    ): Flow<GetSubLocationAirportState> = flow {

        val response = airportAssignmentRepository.getSubLocationAssignmentByLocationId(
            locationId, showWingsChild, versionCode
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val tempCountSubLocationItem = ArrayList<CountSubLocationItem>()

        response.subLocationItemsList.forEach {
            tempCountSubLocationItem.add(
                CountSubLocationItem(
                    subLocationName = it.subLocationName,
                    count = it.count,
                    subLocationId = it.subLocationId,
                    withPassenger = it.withPassenger
                )
            )
        }

        val result = GetSubLocationAirportModel(
            locationName = response.locationName,
            locationId = response.locationId,
            countSubLocationItem = tempCountSubLocationItem
        )

        emit(GetSubLocationAirportState.Success(result))
    }
}