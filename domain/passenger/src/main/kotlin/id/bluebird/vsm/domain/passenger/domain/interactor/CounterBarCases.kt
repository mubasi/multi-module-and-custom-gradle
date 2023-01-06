package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.CounterBar
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class CounterBarCases (private val queueReceiptRepository: QueueReceiptRepository):
CounterBar {
    override fun invoke(locationId: Long, subLocationId: Long): Flow<CounterBarState> = flow {
        val response = queueReceiptRepository.counterBar(
            locationId,
            subLocationId
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