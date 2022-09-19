package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.ListQueueWaitingState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass

@ExperimentalCoroutinesApi
internal class ListQueueWaitingCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var listQueueWaitingCases: ListQueueWaitingCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        listQueueWaitingCases = ListQueueWaitingCases(queueReceiptRepository)
    }


    @Test
    fun `getWaitingQueueCasesTest, isNotEmpty` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.listQueueWaiting(1) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
                    .apply {
                        count = 1
                    }
                    .build()
            )
        }

        flowOf(listQueueWaitingCases.invoke(1)).test {
            assert(awaitItem().single() is ListQueueWaitingState.Success)
            awaitComplete()
        }

    }


}