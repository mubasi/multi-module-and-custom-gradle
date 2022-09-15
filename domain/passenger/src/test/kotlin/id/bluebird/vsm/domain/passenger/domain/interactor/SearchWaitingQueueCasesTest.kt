package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass


internal class SearchWaitingQueueCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var searchWaitingQueueCases: SearchWaitingQueueCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        searchWaitingQueueCases = SearchWaitingQueueCases(queueReceiptRepository)
    }

    @Test
    fun `searchWaitingQueueCases, isEmpty` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.searchWaitingQueue(
            "aa",
            1,
            1
        ) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseSearchQueue.newBuilder()
                    .build()
            )
        }

        flowOf(searchWaitingQueueCases.invoke(
            "aa",
            1
        )).test {
            Assertions.assertEquals(awaitItem().single(), WaitingQueueState.EmptyResult)
            awaitComplete()
        }
    }
}