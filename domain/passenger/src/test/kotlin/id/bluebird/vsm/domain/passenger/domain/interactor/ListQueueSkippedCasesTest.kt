package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
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
        every { queueReceiptRepository.listQueueSkipped(1, 11) } returns flow {
            val temp = QueuePangkalanOuterClass.ResponseGetSkippedQueue.newBuilder()
            val skipped = mutableListOf<Queue>()
            for(i in 0L until 5L) {
                skipped.add(Queue.newBuilder()
                    .apply {
                       id = i
                    }.build())
            }
            temp.addAllSkipped(skipped)
            emit(temp.build())
        }

        flowOf(listQueueSkippedCases.invoke(1, 11)).test {
            awaitItem().collectLatest {
                val temp = it as ListQueueSkippedState.Success
                for (i in 0 until 5){
                    assert( temp.listQueueResult.queue[i].id == i.toLong())
                }
            }
            awaitComplete()
        }

    }

}
