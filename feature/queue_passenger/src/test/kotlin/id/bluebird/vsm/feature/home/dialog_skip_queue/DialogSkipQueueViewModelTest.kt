package id.bluebird.vsm.feature.home.dialog_skip_queue

import id.bluebird.vsm.domain.passenger.SkipQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SkipQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.SkipQueueResult
import id.bluebird.vsm.feature.home.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DialogSkipQueueViewModelTest {

    private lateinit var subjectUnderTest: DialogSkipQueueViewModel
    private val skipQueue: SkipQueue = mockk()
    private val states = mutableListOf<DialogSkipQueueState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        subjectUnderTest = DialogSkipQueueViewModel(skipQueue)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `cancelSkipQueue, emit CancleDialog`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogSkipQueueState.toList(states)
        }

        //WHEN
        subjectUnderTest.cancelSkipQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogSkipQueueState.CancleDialog, states[0])
    }

    @Test
    fun `prosesDialog, emit ProsesDialog`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogSkipQueueState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDialog()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogSkipQueueState.ProsessDialog, states[0])
    }

    @Test
    fun `prosesSkipQueue, when request is success, emit SuccessDialog`() = runTest {
        //GIVEN
        val skippedQueueId = 1L
        val locationId = 1L
        val subLocationId = 11L
        val nextQueue = Queue(
            3L,
            "AB11",
            "12-12-2022",
            "message",
            "currentQueue",
            10L,
            "12-12-2022",
            subLocationId
        )
        every { skipQueue.invoke(any(), any(), any()) } returns flow {
            emit(SkipQueueState.Success(
                SkipQueueResult(skippedQueueId, nextQueue)
            ))
        }
        val collect = launch {
            subjectUnderTest.dialogSkipQueueState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesSkipQueue(skippedQueueId, locationId, subLocationId)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogSkipQueueState.SuccessDialog, states[0])
    }

    @Test
    fun `prosesSkipQueue, when request throw error, emit FailedDialog`() = runTest {
        //GIVEN
        val skippedQueueId = 1L
        val locationId = 1L
        val subLocationId = 11L
        every { skipQueue.invoke(any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogSkipQueueState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesSkipQueue(skippedQueueId, locationId, subLocationId)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogSkipQueueState.FailedDialog(error), states[0])
    }
}