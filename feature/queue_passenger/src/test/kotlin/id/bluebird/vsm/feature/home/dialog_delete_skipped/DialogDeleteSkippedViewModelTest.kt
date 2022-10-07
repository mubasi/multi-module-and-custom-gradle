package id.bluebird.vsm.feature.home.dialog_delete_skipped

import id.bluebird.vsm.domain.passenger.DeleteSkippedState
import id.bluebird.vsm.domain.passenger.domain.cases.DeleteSkipped
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
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DialogDeleteSkippedViewModelTest {

    private lateinit var subjectUnderTest: DialogDeleteSkippedViewModel
    private val deleteSkipped: DeleteSkipped = mockk()
    private val states = mutableListOf<DialogDeleteSkippedState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        subjectUnderTest = DialogDeleteSkippedViewModel(deleteSkipped)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `prosesDelete, emit state ProsesDeleteQueueSkipped`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogDeleteSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDelete()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogDeleteSkippedState.ProsesDeleteQueueSkipped, states[0])
    }

    @Test
    fun `skipDelete, emit state CancelDeleteQueueSkipped`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogDeleteSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.skipDelete()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogDeleteSkippedState.CancelDeleteQueueSkipped, states[0])
    }

    @Test
    fun `prosesDeleteQueue, when request success, emit state SuccessDeleteSkippedQueue`() = runTest {
        //GIVEN
        every { deleteSkipped.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(DeleteSkippedState.Success(
                QueueResult(
                    "Success",
                    "type",
                    Queue(
                        1L,
                        "AB11",
                        "12-12-2022",
                        "message",
                        "AB11",
                        10L,
                        "12-12-2022",
                        11L
                    )
                )
            ))
        }
        val collect = launch {
            subjectUnderTest.dialogDeleteSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDeleteQueue("AB11", 1L, 1L, 11L)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogDeleteSkippedState.SuccessDeleteQueueSkipped, states[0])
    }

    @Test
    fun `prosesDeleteQueue, when request throw error, emit state FailedDeleteQueueSkipped`() = runTest {
        //GIVEN
        every { deleteSkipped.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogDeleteSkippedState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDeleteQueue("AB11", 1L, 1L, 11L)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogDeleteSkippedState.FailedDeleteQueueSkipped(error), states[0])
    }
}