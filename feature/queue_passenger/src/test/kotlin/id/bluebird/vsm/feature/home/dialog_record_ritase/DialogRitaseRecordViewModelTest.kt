package id.bluebird.vsm.feature.home.dialog_record_ritase

import android.text.Html
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.passenger.TakeQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.TakeQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.TakeQueueResult
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.CurrentQueueCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DialogRecordRitaseViewModelTest {
    private lateinit var subjectUnderTest: DialogRecordRitaseViewModel
    private val takeQueue: TakeQueue = mockk()
    private val states = mutableListOf<DialogRecordRitaseState>()

    @BeforeEach
    fun setUp() {
        mockkObject(UserUtils)
        mockkStatic(Html::class)
        subjectUnderTest = DialogRecordRitaseViewModel(takeQueue)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `initTest, when set currentQueue is Success and fleetNumber is Empty`() = runTest {
        val locationId: Long = 1
        val subLocationId: Long = 2
        val userId: Long = 3
        val currentQueueCache = CurrentQueueCache(
            id = 1,
            number = "aa",
            createdAt = "bb"
        )
        val fleetNumber = ""

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }
        //WHEN
        subjectUnderTest.init(currentQueueCache, locationId, subLocationId, fleetNumber, userId)
        runCurrent()
        delay(500)

        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(DialogRecordRitaseState.ProgressDialog, states[0])
        Assertions.assertEquals(DialogRecordRitaseState.Idle, states[1])
        Assertions.assertEquals(userId, subjectUnderTest.userId)
        Assertions.assertEquals(locationId, subjectUnderTest.locationId)
        Assertions.assertEquals(subLocationId, subjectUnderTest.subLocationId)
        Assertions.assertEquals(fleetNumber, subjectUnderTest.fleetNumber)
        Assertions.assertEquals(currentQueueCache, subjectUnderTest.currentQueue.value)
        collect.cancel()
    }

    @Test
    fun cancleDialogTest() = runTest {
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.cancleDialog()
        runCurrent()
        delay(500)

        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(DialogRecordRitaseState.CancelDialog, states[0])
        collect.cancel()
    }

    @Test
    fun selectFleetTest() = runTest {
        val locationId: Long = 1
        val subLocationId: Long = 2
        val userId: Long = 3

        subjectUnderTest.setValLocationId(locationId)
        subjectUnderTest.setValSubLocationId(subLocationId)
        subjectUnderTest.setValUserId(userId)

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.selectFleet()
        runCurrent()
        delay(500)

        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(
            DialogRecordRitaseState.SelectFleet(
                userId = userId,
                locationId = locationId,
                subLocationId = subLocationId
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `prosesDialogTest when fleetNumber is Empty`() = runTest {

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDialog()
        runCurrent()
        delay(500)

        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(DialogRecordRitaseState.FleetEmpty, states[0])
        collect.cancel()
    }

    @Test
    fun `prosesDialogTest when fleetNumber is Not Empty and result error`() = runTest {
        val fleetNumber = "aa"
        val result = Throwable(message = "error")

        subjectUnderTest.setValFleetNumber(fleetNumber)

        every { takeQueue.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDialog()
        runCurrent()
        delay(500)

        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(DialogRecordRitaseState.ProgressDialog, states[0])
        Assertions.assertEquals(DialogRecordRitaseState.OnError(result, fleetNumber), states[1])
        collect.cancel()
    }

    @Test
    fun `prosesDialogTest when fleetNumber is Not Empty and result success`() = runTest {
        val fleetNumber = "aa"
        val currentQueueCache = CurrentQueueCache(
            id = 1,
            number = "aa",
            createdAt = "bb"
        )

        subjectUnderTest.setValFleetNumber(fleetNumber)
        subjectUnderTest.setValCurrentQueue(currentQueueCache)

        every { takeQueue.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(
                TakeQueueState.Success(
                    TakeQueueResult(
                        message = "aa",
                        queueType = "bb",
                        queue = Queue(
                            id = 1,
                            number = "cc",
                            createdAt = "dd",
                            message = "ee",
                            currentQueue = "ff",
                            totalQueue = 2,
                            timeOrder = "gg",
                            subLocationId = 3
                        )
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDialog()
        runCurrent()
        delay(500)

        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(DialogRecordRitaseState.ProgressDialog, states[0])
        Assertions.assertEquals(DialogRecordRitaseState.SuccessRitase(fleetNumber, currentQueueCache.number), states[1])
        collect.cancel()
    }


}