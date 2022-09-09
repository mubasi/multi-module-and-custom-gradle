package id.bluebird.mall.domain_location.domain.interactor

import id.bluebird.mall.domain_location.GetLocationsWithSubState
import kotlinx.coroutines.flow.Flow

interface GetLocationsWithSub {
    operator fun invoke(): Flow<GetLocationsWithSubState>
}