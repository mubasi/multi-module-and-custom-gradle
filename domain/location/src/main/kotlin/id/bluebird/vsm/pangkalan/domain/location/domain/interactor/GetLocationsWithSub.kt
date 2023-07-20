package id.bluebird.vsm.domain.location.domain.interactor

import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import kotlinx.coroutines.flow.Flow

interface GetLocationsWithSub {
    operator fun invoke(): Flow<GetLocationsWithSubState>
}