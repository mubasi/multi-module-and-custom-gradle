package id.bluebird.vsm.domain.airport_location.domain.cases

import id.bluebird.vsm.domain.airport_location.GetListSublocationAirportState
import kotlinx.coroutines.flow.Flow

interface GetListSublocationAirport {
    operator fun invoke(
        locationId : Long,
        showDeposition: Boolean,
        showWingsChild: Boolean
    ) : Flow<GetListSublocationAirportState>
}