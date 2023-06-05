package id.bluebird.vsm.domain.airport_location.domain.cases

import id.bluebird.vsm.domain.airport_location.GetLocationAirportState
import kotlinx.coroutines.flow.Flow

interface GetLocationAirport {
    operator fun invoke() : Flow<GetLocationAirportState>
}