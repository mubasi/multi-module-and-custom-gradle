package id.bluebird.vsm.feature.queue_fleet.request_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
        mockkStatic(Hawk::class)
        _vm = RequestFleetDialogViewModel(_requestFleet)
    }

    @Test
    fun `requestFleet, given subLocationId, result RequestSuccess count`() = runTest {
        // Given
        val events = mutableListOf<RequestFleetDialogState>()
        _vm.initSubLocationId(100)
        // Mock
        every { UserUtils.getLocationId() } returns 10
        every { _requestFleet.invoke(any(), any(), any()) } returns flow {
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
            // Mock
            every { UserUtils.getLocationId() } returns 10
            every { _requestFleet.invoke(any(), any(), any()) } returns flow {
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
            // Mock
            every { UserUtils.getLocationId() } returns 10
            every { _requestFleet.invoke(any(), any(), any()) } returns flow {
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
        _vm.counter.value = "10"
        val events = mutableListOf<RequestFleetDialogState>()

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.addCounter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals("11", _vm.counter.value)
        Assertions.assertEquals(
            RequestFleetDialogState.FocusState(false),
            events.last()
        )
    }

    @Test
    fun `addCounter, given counter isEmpty, result counter became 1`() = runTest {
        // Given
        _vm.counter.value = ""
        val events = mutableListOf<RequestFleetDialogState>()

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.addCounter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals("1", _vm.counter.value)
        Assertions.assertEquals(
            RequestFleetDialogState.FocusState(false),
            events.last()
        )
    }


    @Test
    fun `minusCounter, given counter is 10, result counter became 9`() = runTest {
        // Given
        _vm.counter.value = "10"
        val events = mutableListOf<RequestFleetDialogState>()

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.minusCounter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals("9", _vm.counter.value)
        Assertions.assertEquals(
            RequestFleetDialogState.FocusState(false),
            events.last()
        )
    }

    @Test
    fun `minusCounter, given counter is 1, result counter is not change`() = runTest {
        // Given
        val events = mutableListOf<RequestFleetDialogState>()

        // Execute
        val job = launch {
            _vm.requestFleetDialogState.toList(events)
        }
        _vm.minusCounter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals("1", _vm.counter.value)
        Assertions.assertEquals(
            RequestFleetDialogState.FocusState(false),
            events.last()
        )
    }

}