package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass
import proto.QueuePangkalanOuterClass.Queue

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
    fun `getWaitingQueueCasesTest, isEmpty`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.getWaitingQueue(1, 11) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
                    .apply {
                        count = 0
                    }
                    .build()
            )
        }

        flowOf(getWaitingQueueCases.invoke(1, 11)).test {
            Assertions.assertEquals(awaitItem().single(), WaitingQueueState.EmptyResult)
            awaitComplete()
        }

    }


    @Test
    fun `getWaitingQueueCasesTest, isNotEmpty`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.getWaitingQueue(1, 11) } returns flow {
            val temp = QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
            val tempWaitings = mutableListOf<Queue>()
            for (i in 0 until 5) {
                tempWaitings.add(Queue.newBuilder().setId(i.toLong()).build())
            }
            temp.count = 5
            temp.addAllWaiting(tempWaitings)
            emit(temp.build())
        }

        flowOf(getWaitingQueueCases.invoke(1, 11)).test {
            awaitItem().collectLatest {
                assert(it is WaitingQueueState.Success)
                for (i in 0 until 5){
                    assert((it as WaitingQueueState.Success).waitingQueue[i].id == i.toLong())
                }
            }
            awaitComplete()
        }
    }
}