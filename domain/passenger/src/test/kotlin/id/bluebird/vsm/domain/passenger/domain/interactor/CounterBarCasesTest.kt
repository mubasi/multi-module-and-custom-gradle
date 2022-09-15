package id.bluebird.vsm.domain.passenger.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
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
internal class CounterBarCasesTest {

    private val queueReceiptRepository: QueueReceiptRepository = mockk()
    private lateinit var counterBarCases: CounterBarCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        counterBarCases = CounterBarCases(queueReceiptRepository)
    }

    @Test
    fun `counterBarCaseTest, isSuccess` () = runTest {
        //mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { queueReceiptRepository.counterBar(1) } returns flow {
            emit(
                QueuePangkalanOuterClass.responseGetCountQueue.newBuilder()
                    .apply {
                        locationId = 2
                    }.build()
            )
        }

        flowOf(counterBarCases.invoke(1)).test {
            Assertions.assertEquals(
                awaitItem().single(),
                CounterBarState.Success(
                    CounterBarResult(
                        locationId = 2,
                        ongoing = 0,
                        skipped = 0,
                        ritese = 0,
                        modifiedAt = ""
                    )
                )
            )
            awaitComplete()
        }
    }
}