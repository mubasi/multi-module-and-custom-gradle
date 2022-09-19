package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
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
internal class ListQueueSkippedCasesTest {
    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var listQueueSkippedCases : ListQueueSkippedCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        listQueueSkippedCases = ListQueueSkippedCases(queueReceiptRepository)
    }

    @Test
    fun `listQueueSkippedCasesTest, isNotEmpty` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.listQueueSkipped(1) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseGetSkippedQueue.newBuilder()
                    .build()
            )
        }

        flowOf(listQueueSkippedCases.invoke(1)).test {
            assert(awaitItem().single() is ListQueueSkippedState.Success)
            awaitComplete()
        }

    }


}
