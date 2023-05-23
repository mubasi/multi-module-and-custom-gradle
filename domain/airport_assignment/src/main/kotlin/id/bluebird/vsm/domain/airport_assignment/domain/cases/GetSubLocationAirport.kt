package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
import kotlinx.coroutines.flow.Flow

interface GetSubLocationAirport {
    operator fun invoke(
       locationId : Long, showWingsChild: Boolean, versionCode: Long
    ) : Flow<GetSubLocationAirportState>
}