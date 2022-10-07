package id.bluebird.vsm.feature.home.dialog_queue_receipt

import android.text.Html
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.passenger.GetQueueReceiptState
import id.bluebird.vsm.domain.passenger.TakeQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.GetQueueReceipt
import id.bluebird.vsm.domain.passenger.domain.cases.TakeQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.QueueResult
import id.bluebird.vsm.domain.passenger.model.TakeQueueResult
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.TakeQueueCache
import id.bluebird.vsm.feature.home.model.UserInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
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
internal class DialogQueueReceiptViewModelTest {

    private lateinit var subjectUnderTest: DialogQueueReceiptViewModel
    private val getQueueReceipt: GetQueueReceipt = mockk()
    private val takeQueueReceipt: TakeQueue = mockk()
    private val getUserId : GetUserId = mockk()
    private val states = mutableListOf<DialogQueueReceiptState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        mockkObject(UserUtils)
        mockkStatic(Html::class)
        subjectUnderTest = DialogQueueReceiptViewModel(getQueueReceipt, takeQueueReceipt, getUserId)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `init, when response success, emit GetUserInfoSuccess`() = runTest {
        //GIVEN
        val createUserResult = CreateUserResult(
            1L,
            "test name",
            "test username",
            1L,
            1L,
            "location name",
            listOf(11L),
            "subLocation name"
        )
        every { UserUtils.getUserId() } returns 1L
        every { getUserId.invoke(any()) } returns flow {
            emit(GetUserByIdState.Success(
                createUserResult
            ))
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.init()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetUser, states[0])
        assertEquals(DialogQueueReceiptState.GetUserInfoSuccess, states[1])
        assertEquals(UserInfo(1L, 1L, 11L), subjectUnderTest.userInfo)
    }

    @Test
    fun `init, when response throw error, emit FailedGetUser`() = runTest {
        //GIVEN
        every { UserUtils.getUserId() } returns 1L
        every { getUserId.invoke(any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.init()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetUser, states[0])
        assertEquals(DialogQueueReceiptState.FailedGetUser(error), states[1])
    }

    @Test
    fun `getQueue, when response success, emit GetQueueSuccess`() = runTest {
        //GIVEN
        subjectUnderTest.setUserInfo(UserInfo(1L, 1L, 11L))
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
        every { getQueueReceipt.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(GetQueueReceiptState.Success(
                QueueResult("message", "queue type", queue)
            ))
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.getQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetQueue, states[0])
        assertEquals(DialogQueueReceiptState.GetQueueSuccess, states[1])
    }

    @Test
    fun `getQueue, when response throw error, emit FailedGetQueue`() = runTest {
        //GIVEN
        subjectUnderTest.setUserInfo(UserInfo(1L, 1L, 11L))
        every { getQueueReceipt.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.getQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetQueue, states[0])
        assertEquals(DialogQueueReceiptState.FailedGetQueue(error), states[1])
    }

    @Test
    fun `requestQueue, when response success, emit TakeQueueSuccess`() = runTest {
        //GIVEN
        subjectUnderTest.setUserInfo(UserInfo(1L, 1L, 11L))
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
        every { takeQueueReceipt.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(TakeQueueState.Success(TakeQueueResult("message", "queueType", queue)))
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.requestQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetQueue, states[0])
        assertEquals(DialogQueueReceiptState.TakeQueueSuccess, states[1])
        assertEquals(
            TakeQueueCache(1L, "AB11", "12-12-2022", "message", "AB11", 2L, 11L),
            subjectUnderTest.takeQueueCache
        )
    }

    @Test
    fun `requestQueue, when response throw error, emit FailedGetQueue`() = runTest {
        //GIVEN
        subjectUnderTest.setUserInfo(UserInfo(1L, 1L, 11L))
        every { takeQueueReceipt.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.requestQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogQueueReceiptState.ProgressGetQueue, states[0])
        assertEquals(DialogQueueReceiptState.FailedTakeQueue(error), states[1])
    }

    @Test
    fun `cancelDialog, emit CancelDialog`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.dialogQueueReceiptState.toList(states)
        }

        //WHEN
        subjectUnderTest.cancelDialog()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogQueueReceiptState.CancelDialog, states[0])
    }
}