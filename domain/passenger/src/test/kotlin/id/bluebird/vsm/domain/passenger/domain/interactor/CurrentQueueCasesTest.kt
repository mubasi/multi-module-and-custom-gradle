package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
import id.bluebird.vsm.domain.passenger.model.CurrentQueueResult
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
internal class CurrentQueueCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var currentQueueCases: CurrentQueueCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        currentQueueCases = CurrentQueueCases(queueReceiptRepository)
    }

    @Test
    fun `currentQueueCasesTest, isSuccess` () = runTest {
        //mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.getCurrentQueue(1, 11) } returns flow {
            emit(
                QueuePangkalanOuterClass.GetCurrentQueueResponse.newBuilder()
                    .apply {
                        1
                    }.build()
            )
        }

        flowOf(currentQueueCases.invoke(1, 11)).test {
            assertEquals(
                awaitItem().single(),
                GetCurrentQueueState.Success(
                    CurrentQueueResult(
                        0,
                        "",
                        ""
                    )
                )
            )
            awaitComplete()
        }
    }

}
