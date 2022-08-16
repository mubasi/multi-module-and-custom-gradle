package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.CounterBarState
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.domain.cases.CounterBar
import id.bluebird.mall.domain_pasenger.model.CounterBarResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class CounterBarCases (private val queueReceiptRepository: QueueReceiptRepository):
CounterBar {
    override fun invoke(locationId: Long): Flow<CounterBarState> = flow {
        val response = queueReceiptRepository.counterBar(
            locationId
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val result = CounterBarResult(
            locationId = response.locationId,
            ongoing = response.ongoing,
            skipped = response.skipped,
            ritese = response.ritase,
            modifiedAt = response.modifiedAt
        )
        emit(CounterBarState.Success(result))
    }
}