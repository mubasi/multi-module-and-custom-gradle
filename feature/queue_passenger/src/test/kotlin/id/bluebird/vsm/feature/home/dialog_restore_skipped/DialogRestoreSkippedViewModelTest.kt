package id.bluebird.vsm.feature.home.dialog_restore_skipped

import id.bluebird.vsm.domain.passenger.RestoreSkippedState
import id.bluebird.vsm.domain.passenger.domain.cases.RestoreSkipped
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.QueueResult
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
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DialogRestoreSkippedViewModelTest {

    private lateinit var subjectUnderTest: DialogRestoreSkippedViewModel
    private val restoreSkipped: RestoreSkipped = mockk()
    private val states = mutableListOf<DialogRestoreSkippedState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        subjectUnderTest = DialogRestoreSkippedViewModel(restoreSkipped)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `prosesRestore, emit ProsesRestoreQueueSkipped`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogRestoreSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesRestore()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRestoreSkippedState.ProsesRestoreQueueSkipped, states[0])
    }

    @Test
    fun `prosesRestoreQueue, when response success, emit SuccessRestoreQueueSkipped`() = runTest {
        //GIVEN
        val number = "AB11"
        val queueId = 21L
        val locationId = 1L
        val subLocationId = 11L
        val queue = Queue(
            1L,
            "AB11",
            "12-12-2022",
            "message",
            "AB11",
            2L,
            "12-12-2022",
            11L
        )
        every { restoreSkipped.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(RestoreSkippedState.Success(QueueResult("message", "queueType", queue)))
        }
        val collect = launch {
            subjectUnderTest.dialogRestoreSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesRestoreQueue(number, queueId, locationId, subLocationId)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRestoreSkippedState.SuccessRestoreQueueSkipped, states[0])
    }

    @Test
    fun `prosesRestoreQueue, when response throw error, emit FailedRestoreQueueSkipped`() = runTest {
        //GIVEN
        val number = "AB11"
        val queueId = 21L
        val locationId = 1L
        val subLocationId = 11L
        every { restoreSkipped.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogRestoreSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesRestoreQueue(number, queueId, locationId, subLocationId)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRestoreSkippedState.FailedRestoreQueueSkipped(error), states[0])
    }

    @Test
    fun `restoreSkipped, emit CancelRestoreQueueSkipped`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogRestoreSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.restoreSkipped()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRestoreSkippedState.CancelRestoreQueueSkipped, states[0])
    }
}