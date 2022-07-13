package id.bluebird.mall.feature_queue_fleet.request_fleet

import id.bluebird.mall.domain_fleet.RequestState
import id.bluebird.mall.domain_fleet.domain.cases.RequestFleet
import id.bluebird.mall.feature_queue_fleet.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class RequestFleetDialogViewModelTest {

    private val _requestFleet: RequestFleet = mockk()
    private lateinit var _vm: RequestFleetDialogViewModel

    @BeforeEach
    fun setup() {
        _vm = RequestFleetDialogViewModel(_requestFleet)
    }

    @Test
    fun `requestFleet, given subLocationId, result RequestSuccess count`() = runTest {
        // Given
        val events = mutableListOf<RequestFleetDialogState>()
        _vm.initSubLocationId(100)
        _vm.counter.value = 11

        // Mock
        every { _requestFleet.invoke(any(), any()) } returns flow {
            emit(RequestState.Success(11))
        }

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.requestFleet()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(1, events.size)
        Assertions.assertEquals(RequestFleetDialogState.RequestSuccess(11), events.last())
    }

    @Test
    fun `requestFleet, given subLocationId, result MessageError with message INVALID_COUNTER`() =
        runTest {
            // Given
            val events = mutableListOf<RequestFleetDialogState>()
            _vm.counter.value = 0

            // Mock
            every { _requestFleet.invoke(any(), any()) } returns flow {
                emit(RequestState.CountInvalid)
            }

            // Execute
            val job = launch {
                _vm.requestFleetDialogState.toList(events)
            }
            _vm.requestFleet()
            runCurrent()
            job.cancel()

            // Result
            Assertions.assertEquals(1, events.size)
            Assertions.assertEquals(
                RequestFleetDialogState.MessageError(RequestFleetDialogViewModel.INVALID_COUNTER),
                events.last()
            )
        }

    @Test
    fun `requestFleet, given subLocationId, result MessageError with message INVALID_SUB_LOCATION`() =
        runTest {
            // Given
            val events = mutableListOf<RequestFleetDialogState>()
            _vm.initSubLocationId(-1)
            _vm.counter.value = 10

            // Mock
            every { _requestFleet.invoke(any(), any()) } returns flow {
                emit(RequestState.SubLocationInvalid)
            }

            // Execute
            val job = launch {
                _vm.requestFleetDialogState.toList(events)
            }
            _vm.requestFleet()
            runCurrent()
            job.cancel()

            // Result
            Assertions.assertEquals(1, events.size)
            Assertions.assertEquals(
                RequestFleetDialogState.MessageError(RequestFleetDialogViewModel.INVALID_SUB_LOCATION),
                events.last()
            )
        }

    @Test
    fun `cancelFleet, result CancelDialog is called`() = runTest {
        // Given
        val events = mutableListOf<RequestFleetDialogState>()

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.cancelFleetDialog()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(1, events.size)
        Assertions.assertEquals(RequestFleetDialogState.CancelDialog, events.last())
    }

    @Test
    fun `addCounter, given counter is 10, result counter became 11`() = runTest {
        // Given
        _vm.counter.value = 10

        // Execute
        _vm.addCounter()

        // Result
        Assertions.assertEquals(11, _vm.counter.value)
    }

    @Test
    fun `minusCounter, given counter is 10, result counter became 9`() = runTest {
        // Given
        _vm.counter.value = 10

        // Execute
        _vm.minusCounter()

        // Result
        Assertions.assertEquals(9, _vm.counter.value)
    }

    @Test
    fun `minusCounter, given counter is 1, result counter is not change`() = runTest {
        // Given
        _vm.counter.value = 1

        // Execute
        _vm.minusCounter()

        // Result
        Assertions.assertEquals(1, _vm.counter.value)
    }
}