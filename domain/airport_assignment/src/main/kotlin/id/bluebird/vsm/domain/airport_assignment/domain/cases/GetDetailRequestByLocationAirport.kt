package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.GetDetailRequestInLocationAirportState
import kotlinx.coroutines.flow.Flow

interface GetDetailRequestByLocationAirport {
    operator fun invoke(
        locationId : Long,
        showWingsChild : Boolean
    ) : Flow<GetDetailRequestInLocationAirportState>
}