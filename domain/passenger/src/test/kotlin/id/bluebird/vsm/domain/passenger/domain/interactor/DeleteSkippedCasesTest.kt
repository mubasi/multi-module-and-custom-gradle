package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.DeleteSkippedState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.QueueResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.QueuePangkalanOuterClass

@ExperimentalCoroutinesApi
internal class DeleteSkippedCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var deleteSkippedCases: DeleteSkippedCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        deleteSkippedCases = DeleteSkippedCases(queueReceiptRepository)
    }

    @Test
    fun `deleteSkippedCasesTest, isSuccess` () = runTest {
        //mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.deleteSkippedQueue(
            queueId = 1,
            queueType = 2,
            locationId = 3,
            queueNumber = "aa",
            subLocationId = 4,
            fleetNumber = "bb"
        ) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseQueues.newBuilder()
                    .apply {
                        message = "abc"
                        queueType = "DELETE_SKIPPED_QUEUE"
                    }
                    .build()
            )
        }

        flowOf(deleteSkippedCases.invoke(1, 2,3, "aa", 4, "bb")).test {
            assertEquals(
                awaitItem().single(),
                DeleteSkippedState.Success(
                    QueueResult(
                        "abc",
                        "DELETE_SKIPPED_QUEUE",
                        Queue(
                            id = 0,
                            number = "",
                            createdAt = "",
                            message = "",
                            currentQueue = "",
                            totalQueue = 0,
                            timeOrder = "",
                            subLocationId = 0
                        ),
                    )
                )
            )
            awaitComplete()
        }
    }
}