package id.bluebird.vsm.feature.home.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
import id.bluebird.vsm.domain.passenger.ListQueueWaitingState
import id.bluebird.vsm.domain.passenger.domain.cases.CounterBar
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueSkipped
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueWaiting
import id.bluebird.vsm.domain.passenger.model.CounterBarResult
import id.bluebird.vsm.domain.passenger.model.CurrentQueueResult
import id.bluebird.vsm.domain.passenger.model.ListQueueResult
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.*
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class QueuePassengerViewModelTest {

    @Rule
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getUserAssignment: GetUserAssignment = mockk(relaxed = true)
    private val currentQueue: CurrentQueue = mockk()
    private val listQueueWaiting: ListQueueWaiting = mockk()
    private val listQueueSkipped: ListQueueSkipped = mockk()
    private val counterBar: CounterBar = mockk()
    private lateinit var subjectUnderTest: QueuePassengerViewModel
    private val states = mutableListOf<QueuePassengerState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        mockkObject(LocationNavigationTemporary)
        subjectUnderTest = QueuePassengerViewModel(
            getUserAssignment,
            currentQueue,
            listQueueWaiting,
            listQueueSkipped,
            counterBar
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `init, when user is not officer and LocationNav not available, emit toSelectLocation`() =
        runTest {
            //GIVEN
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns false
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.init()
            runCurrent()
            delay(500)

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProgressHolder, states[0])
            assertEquals(QueuePassengerState.ToSelectLocation, states[1])
            collect.cancel()
        }

    @Test
    fun `init, when user is officer and locationNav not available and failed to getUser, emit failedGetUser`() =
        runTest {
            //GIVEN
            val titleString = "..."
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns true
            every { UserUtils.getUserId() } returns 1L
            every { getUserAssignment.invoke(any()) } returns flow {
                throw NullPointerException(error)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.init()
            runCurrent()
            val resultTitle = subjectUnderTest.titleLocation.getOrAwaitValue()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesGetUser, states[0])
            assertEquals(QueuePassengerState.FailedGetUser(error), states[1])
            assertEquals(UserInfo(), subjectUnderTest.mUserInfo)
            assertEquals(titleString, resultTitle)

            collect.cancel()
        }

    @Test
    fun `init, when user is officer and locationNav not available and failed to getUser from State, emit failedGetUser`() =
        runTest {
            //GIVEN
            val titleString = "..."
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns true
            every { UserUtils.getUserId() } returns 1L
            every { getUserAssignment.invoke(any()) } returns flow {
                emit(GetUserAssignmentState.UserNotFound)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.init()
            runCurrent()
            val resultTitle = subjectUnderTest.titleLocation.getOrAwaitValue()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesGetUser, states[0])
            assertEquals(
                QueuePassengerState.FailedGetUser(QueuePassengerViewModel.ERROR_MESSAGE_UNKNOWN),
                states[1]
            )
            assertEquals(UserInfo(), subjectUnderTest.mUserInfo)
            assertEquals(titleString, resultTitle)

            collect.cancel()
        }

    @Test
    fun `getCurrentQueue, when currentQueue response success, emit state SuccessCurrentQueue`() =
        runTest {
            //GIVEN
            val id = 1L
            val number = "AB11"
            val createdDate = "09-09-2020"
            every { currentQueue.invoke(any(), any()) } returns flow {
                emit(
                    GetCurrentQueueState.Success(
                        CurrentQueueResult(id, number, createdDate)
                    )
                )
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }
            //WHEN
            subjectUnderTest.getCurrentQueue()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesCurrentQueue, states[0])
            assertEquals(QueuePassengerState.SuccessCurrentQueue, states[1])
            assertEquals(
                CurrentQueueCache(id, number, createdDate),
                subjectUnderTest.currentQueueCache
            )
            assertEquals(number, subjectUnderTest.currentQueueNumber.getOrAwaitValue())
        }

    @Test
    fun `getCurrentQueue, when currentQueue response throw error, emit state FailedCurrentQueue`() =
        runTest {
            //GIVEN
            every { currentQueue.invoke(any(), any()) } returns flow {
                throw NullPointerException(error)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }
            //WHEN
            subjectUnderTest.getCurrentQueue()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesCurrentQueue, states[0])
            assertEquals(QueuePassengerState.FailedCurrentQueue(error), states[1])
        }

    @Test
    fun `getListQueue, when listQueueWaiting response success, emit state SuccessListQueue`() =
        runTest {
            //GIVEN
            val responseCount = 3
            val resultCount = 2
            val queueId = 1L
            val queueNumber = "AB11"
            val queue = Queue(
                queueId,
                queueNumber,
                "10-10-2022",
                "message",
                "AB11",
                responseCount.toLong(),
                "10-10-2022",
                11L
            )
            every { listQueueWaiting.invoke(any(), any()) } returns flow {
                emit(
                    ListQueueWaitingState.Success(
                        ListQueueResult(
                            responseCount.toLong(),
                            ArrayList(List(responseCount) { queue })
                        )
                    )
                )
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getListQueue()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesListQueue, states[0])
            assertEquals(QueuePassengerState.SuccessListQueue, states[1])
            assertEquals(
                ListQueueResultCache(
                    resultCount.toLong(),
                    ArrayList(List(resultCount) { QueueReceiptCache(queueId, queueNumber) })
                ), subjectUnderTest.listQueueWaitingCache
            )
            assertEquals(
                resultCount.toString(),
                subjectUnderTest.waitingQueueCount.getOrAwaitValue()
            )
        }

    @Test
    fun `getListQueue, when listQueueWaiting response throw error, emit state SuccessListQueue`() =
        runTest {
            //GIVEN
            every { listQueueWaiting.invoke(any(), any()) } returns flow {
                throw NullPointerException(error)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getListQueue()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesListQueue, states[0])
            assertEquals(QueuePassengerState.FailedListQueue(error), states[1])
        }

    @Test
    fun `getListQueueSkipped, when listQueueSkipped response success, emit state SuccessListQueue`() =
        runTest {
            //GIVEN
            val responseCount = 3
            val queueId = 1L
            val queueNumber = "AB11"
            val queue = Queue(
                queueId,
                queueNumber,
                "10-10-2022",
                "message",
                "AB11",
                responseCount.toLong(),
                "10-10-2022",
                11L
            )
            every { listQueueSkipped.invoke(any(), any()) } returns flow {
                emit(
                    ListQueueSkippedState.Success(
                        ListQueueResult(
                            responseCount.toLong(),
                            ArrayList(List(responseCount) { queue })
                        )
                    )
                )
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getListQueueSkipped()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesListQueueSkipped, states[0])
            assertEquals(QueuePassengerState.SuccessListQueueSkipped, states[1])
            assertEquals(
                ListQueueResultCache(
                    responseCount.toLong(),
                    ArrayList(List(responseCount) { QueueReceiptCache(queueId, queueNumber) })
                ), subjectUnderTest.listQueueSkippedCache
            )
            assertEquals(
                responseCount.toString(),
                subjectUnderTest.skippedQueueCount.getOrAwaitValue()
            )
        }

    @Test
    fun `getListQueueSkipped, when listQueueWaiting response throw error, emit state SuccessListQueue`() =
        runTest {
            //GIVEN
            every { listQueueSkipped.invoke(any(), any()) } returns flow {
                throw NullPointerException(error)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getListQueueSkipped()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesListQueueSkipped, states[0])
            assertEquals(QueuePassengerState.FailedListQueueSkipped(error), states[1])
        }

    @Test
    fun `prosesSkipQueue, emit state ProsesSkipQueue`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.queuePassengerState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesSkipQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueuePassengerState.ProsesSkipQueue, states[0])
    }

    @Test
    fun `prosesQueue, emit state ProsesQueue`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.queuePassengerState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueuePassengerState.ProsesQueue, states[0])
    }

    @Test
    fun `prosesDeleteQueue, with given queueReceiptCache, emit state ProsesDeleteQueueSkip with given item`() =
        runTest {
            //GIVEN
            val cache = QueueReceiptCache(1L, "AB11")
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.prosesDeleteQueue(cache)
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(1, states.size)
            assertEquals(QueuePassengerState.ProsesDeleteQueueSkipped(cache), states[0])
        }

    @Test
    fun `prosesRestoreQueue, with given queueReceiptCache, emit state ProsesRestoreQueueSkip with given item`() =
        runTest {
            //GIVEN
            val cache = QueueReceiptCache(1L, "AB11")
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.prosesRestoreQueue(cache)
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(1, states.size)
            assertEquals(QueuePassengerState.ProsesRestoreQueueSkipped(cache), states[0])
        }

    @Test
    fun `prosesSearchQueue, emit state SearchQueue when prefix not null`() = runTest {
        //GIVEN
        subjectUnderTest.mUserInfo = UserInfo(1L, 1L, 11L)

        val dataWaiting = ArrayList(List(1) { QueueReceiptCache(1L, "aa") })
        val listWaiting = ListQueueResultCache(
            dataWaiting.size.toLong(),
            dataWaiting
        )

        val dataSkipping = ArrayList(List(1) { QueueReceiptCache(2L, "bb") })
        val listSkipping = ListQueueResultCache(
            dataSkipping.size.toLong(),
            dataSkipping
        )

        subjectUnderTest.setPrefix("cc")
        subjectUnderTest.setListQueueWaiting(listWaiting)
        subjectUnderTest.setListQueueSkipped(listSkipping)

        val collect = launch {
            subjectUnderTest.queuePassengerState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(
            QueuePassengerState.ToSearchQueue(
                1L,
                11L,
                "cc",
                listWaiting.queue,
                listSkipping.queue
            ), states[0]
        )
    }

    @Test
    fun `prosesSearchQueue, emit state SearchQueue when prefix is null`() = runTest {
        //GIVEN
        subjectUnderTest.mUserInfo = UserInfo(1L, 1L, 11L)

        val dataWaiting = ArrayList(List(1) { QueueReceiptCache(1L, "aa") })
        val listWaiting = ListQueueResultCache(
            dataWaiting.size.toLong(),
            dataWaiting
        )

        val dataSkipping = ArrayList(List(1) { QueueReceiptCache(2L, "bb") })
        val listSkipping = ListQueueResultCache(
            dataSkipping.size.toLong(),
            dataSkipping
        )

        subjectUnderTest.setPrefix(null)
        subjectUnderTest.setListQueueWaiting(listWaiting)
        subjectUnderTest.setListQueueSkipped(listSkipping)

        val collect = launch {
            subjectUnderTest.queuePassengerState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchQueue()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(
            QueuePassengerState.ToSearchQueue(
                1L,
                11L,
                "",
                listWaiting.queue,
                listSkipping.queue
            ), states[0]
        )
    }

    @Test
    fun `getCounterBar, when counterBar response success, emit state SuccessCounterBar`() =
        runTest {
            //GIVEN
            every { counterBar.invoke(any(), any()) } returns flow {
                emit(
                    CounterBarState.Success(
                        CounterBarResult(
                            locationId = 1L,
                            ongoing = 1L,
                            skipped = 2L,
                            ritese = 3L,
                            modifiedAt = "10-10-2022"
                        )
                    )
                )
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getCounterBar()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesCounterBar, states[0])
            assertEquals(QueuePassengerState.SuccessCounterBar, states[1])
            assertEquals(
                CounterBarCache(1L, 1L, 2L, 3L, "10-10-2022"),
                subjectUnderTest.currentCounterBar.getOrAwaitValue()
            )
        }

    @Test
    fun `getCounterBar, when counterBar response throw error, emit state FailedCounterBar`() =
        runTest {
            //GIVEN
            every { counterBar.invoke(any(), any()) } returns flow {
                throw NullPointerException(error)
            }
            val collect = launch {
                subjectUnderTest.queuePassengerState.toList(states)
            }

            //WHEN
            subjectUnderTest.getCounterBar()
            runCurrent()
            collect.cancel()

            //THEN
            assertEquals(2, states.size)
            assertEquals(QueuePassengerState.ProsesCounterBar, states[0])
            assertEquals(QueuePassengerState.FailedCounterBar(error), states[1])
        }

    @Test
    fun `qrCodeScreenTest, emit state QrCodeScreen`() = runTest {
        //GIVEN
        val locationName = "aa"
        val subLocationName = "bb"

        subjectUnderTest.mUserInfo = UserInfo(1L, 1L, 11L)
        subjectUnderTest.setLocationName(locationName)
        subjectUnderTest.setSubLocationName(subLocationName)

        val result = "$locationName $subLocationName"

        val collect = launch {
            subjectUnderTest.queuePassengerState.toList(states)
        }

        //WHEN
        subjectUnderTest.toQrCodeScreen()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(
            QueuePassengerState.ToQrCodeScreen(
                1L, 11L, result
            ), states[0]
        )
    }
}