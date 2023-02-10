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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass
import proto.QueuePangkalanOuterClass.Queue

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
        every { queueReceiptRepository.listQueueWaiting(1, 11) } returns flow {
            val temp = QueuePangkalanOuterClass.ResponseGetWaitingQueue.newBuilder()
            val skipped = mutableListOf<Queue>()
            for(i in 0 until 5){
                skipped.add(Queue.newBuilder().setId(i.toLong()).build())
            }
            temp.addAllWaiting(skipped)
            emit(temp.build())
        }

        flowOf(listQueueWaitingCases.invoke(1, 11)).test {
            awaitItem().collectLatest {
                assert(it is ListQueueWaitingState.Success)
                for (i in 0 until 5){
                    assert(i.toLong() ==  (it as ListQueueWaitingState.Success).listQueueResult.queue[i].id)
                }
            }
            awaitComplete()
        }

    }
}