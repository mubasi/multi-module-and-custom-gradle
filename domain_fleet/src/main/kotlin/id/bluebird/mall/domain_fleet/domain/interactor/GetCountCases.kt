package id.bluebird.mall.domain_fleet.domain.interactor

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.domain_fleet.model.CountResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.time.Instant

class GetCountCases(private val fleetRepository: FleetRepository) : GetCount {
    override fun invoke(subLocationId: Long): Flow<GetCountState> = flow {
        val response = fleetRepository.getCount(
            subLocationId,
            UserUtils.getLocationId(),
            Instant.ofEpochMilli(System.currentTimeMillis()).epochSecond
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val countResult = CountResult(response.stock, response.ritase, response.request)
        emit(GetCountState.Success(countResult))
    }
}