package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass

@ExperimentalCoroutinesApi
internal class GetWaitingQueueCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var getWaitingQueueCases: GetWaitingQueueCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getWaitingQueueCases = GetWaitingQueueCases(queueReceiptRepository)
    }

    @Test
    fun `getWaitingQueueCasesTest, isEmpty` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.getWaitingQueue(1) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
                    .apply {
                        count = 0
                    }
                    .build()
            )
        }

        flowOf(getWaitingQueueCases.invoke(1)).test {
            Assertions.assertEquals(awaitItem().single(), WaitingQueueState.EmptyResult)
            awaitComplete()
        }

    }


    @Test
    fun `getWaitingQueueCasesTest, isNotEmpty` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.getWaitingQueue(1) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
                    .apply {
                        count = 1
                    }
                    .build()
            )
        }

        flowOf(getWaitingQueueCases.invoke(1)).test {
            assert(awaitItem().single() is WaitingQueueState.Success)
            awaitComplete()
        }

    }


}