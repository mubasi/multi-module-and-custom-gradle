package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.SkipQueueState
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.SkipQueueResult
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
internal class SkipQueueCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var skipQueueCases: SkipQueueCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        skipQueueCases = SkipQueueCases(queueReceiptRepository)
    }

    @Test
    fun `skipQueueCasesTest, isSuccess` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.skipQueue(
            1,
            2,
            3
        ) } returns flow {
            emit(
                QueuePangkalanOuterClass.ResponseSkipCurrentQueue.newBuilder()
                    .apply {
                        skippedId = 1
                    }
                    .build()
            )
        }

        flowOf(skipQueueCases.invoke(1,2,3)).test {
            Assertions.assertEquals(awaitItem().single(), SkipQueueState.Success(
                SkipQueueResult(
                    skippedId = 1,
                    nextQueue =  Queue(
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
            ))
            awaitComplete()
        }

    }


}