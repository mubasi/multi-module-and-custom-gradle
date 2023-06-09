package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.RequestTaxiDepartState
import kotlinx.coroutines.flow.Flow

interface RequestTaxiDepart {
    operator fun invoke(
        requestFrom : Long,
        locationId : Long,
        count : Long
    ) : Flow<RequestTaxiDepartState>
}