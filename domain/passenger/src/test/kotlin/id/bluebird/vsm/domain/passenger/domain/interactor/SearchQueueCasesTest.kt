package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.SearchQueueState
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
import proto.QueuePangkalanOuterClass.QueueType


@ExperimentalCoroutinesApi
internal class SearchQueueCasesTest {
    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var searchQueueCases: SearchQueueCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        searchQueueCases = SearchQueueCases(queueReceiptRepository)
    }

    @Test
    fun `searchQueueCasesTest, isWaiting and Success` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.searchQueue( "aa", 2, 3, QueueType.SEARCH_WAITING_QUEUE) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseSearchQueue.newBuilder()
                    .build()
            )
        }

        flowOf(searchQueueCases.invoke("aa", 2 ,3,  QueueType.SEARCH_WAITING_QUEUE )).test {
            assert(awaitItem().single() is SearchQueueState.Success)
            awaitComplete()
        }

    }

    @Test
    fun `searchQueueCasesTest, Skipped and Success` () = runTest {
            every { Hawk.get<Long>(any()) } returns 1L
            every { queueReceiptRepository.searchQueue( "aa", 2, 3, QueueType.SEARCH_SKIPPED_QUEUE) } returns flow {
                emit(
                    QueuePangkalanOuterClass.ResponseSearchQueue.newBuilder()
                        .build()
                )
            }

            flowOf(searchQueueCases.invoke("aa", 2 ,3,  QueueType.SEARCH_SKIPPED_QUEUE )).test {
                assert(awaitItem().single() is SearchQueueState.Success)
                awaitComplete()
            }

        }
}