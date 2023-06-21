package id.bluebird.vsm.feature.monitoring.main

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.MonitoringResultState
import id.bluebird.vsm.domain.fleet.domain.cases.Monitoring
import id.bluebird.vsm.domain.fleet.model.MonitoringResult
import id.bluebird.vsm.feature.monitoring.TestCoroutineRule
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class MonitoringViewModelTest {

    private val monitoringUseCases: Monitoring = mockk()
    private lateinit var subjectUnderTest: MonitoringViewModel
    private val states = mutableListOf<MonitoringState>()

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        subjectUnderTest = MonitoringViewModel(monitoringUseCases)
    }

    @AfterEach
    fun resetStates() {
        states.clear()
    }

    @Test
    fun `init when failed get list location`() = runTest {
        val result = Throwable(message = "failed")
        every { monitoringUseCases.invoke() } returns flow {
            throw result
        }

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.init()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(2, states.size)
        assertEquals(MonitoringState.OnProgressGetList, states[0])
        assertEquals(MonitoringState.OnFailedGetList, states[1])
        collect.cancel()
    }

    @Test
    fun `init when get list location failed get data`() = runTest {
        val result = Exception()

        every { UserUtils.getPrivilege() } returns UserUtils.ADMIN
        every { monitoringUseCases.invoke() } returns flow {
            emit(
                MonitoringResultState.Error(result)
            )
        }

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.init()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(2, states.size)
        assertEquals(MonitoringState.OnProgressGetList, states[0])
        assertEquals(MonitoringState.OnFailedGetList, states[1])
        collect.cancel()
    }

    @Test
    fun `init when get list location success get data`() = runTest {
        val temp : ArrayList<MonitoringResult> = ArrayList()
        val result : ArrayList<MonitoringModel> = ArrayList()

        for (i in 1 .. 3) {
            temp.add(
                MonitoringResult(
                    buffer = i,
                    locationName = "aa $i",
                    subLocationName = "bb $i",
                    queueFleet = i,
                    queuePassenger = i,
                    request = i,
                    subLocationId = i.toLong(),
                    totalQueueFleet = i,
                    totalQueuePassenger = i,
                    totalRitase = i,
                    isDeposition = false
                )
            )
            result.add(
                MonitoringModel(
                    subLocationId = i.toLong(),
                    locationName = "aa $i",
                    subLocationName = "bb $i",
                    fleetCount = i,
                    queueCount = i,
                    totalFleetCount = i,
                    totalQueueCount = i,
                    totalRitase = i,
                    fleetRequest = i,
                    buffer = i,
                    editableBuffer = true,
                    isDeposition = false
                )
            )
        }

        every { UserUtils.getPrivilege() } returns UserUtils.ADMIN
        every { monitoringUseCases.invoke() } returns flow {
            emit(
                MonitoringResultState.Success(temp)
            )
        }

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.init()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(2, states.size)
        assertEquals(MonitoringState.OnProgressGetList, states[0])
        assertEquals(MonitoringState.OnSuccessGetList(result), states[1])
        assertEquals(result.size, subjectUnderTest.listLocation.size)
        collect.cancel()
    }

    @Test
    fun toggleNotificationVisibilityTest(){
        assertEquals(true, subjectUnderTest.notificationVisibility.value)
    }

    @Test
    fun `onDialogSaveResultTest when is success`() = runTest {

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.onDialogSaveResult(true, "success")
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.OnSuccessSaveBuffer, states[0])
        collect.cancel()
    }

    @Test
    fun `onDialogSaveResultTest when is not success`() = runTest {
        val result = "not success"

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.onDialogSaveResult(false, result)
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.OnFailedSaveBuffer(result), states[0])
        collect.cancel()
    }

    @Test
    fun `searchScreenTest`() = runTest {
        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchScreen()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.SearchScreen, states[0])
        collect.cancel()
    }

    @Test
    fun `backSearchScreenTest`() = runTest {
        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.backSearchScreen()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.BackSearchScreen, states[0])
        collect.cancel()
    }

    @Test
    fun `filterLocationTest when list is empty`() = runTest {

        subjectUnderTest.params.value = "aa"

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterLocation()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.ErrorFilter, states[0])
        collect.cancel()
    }


    @Test
    fun `filterLocationTest when list is not empty`() = runTest {
        val result : ArrayList<MonitoringModel> = ArrayList()

        for (i in 1 .. 3) {
            result.add(
                MonitoringModel(
                    subLocationId = i.toLong(),
                    locationName = "aa $i",
                    subLocationName = "bb $i",
                    fleetCount = i,
                    queueCount = i,
                    totalFleetCount = i,
                    totalQueueCount = i,
                    totalRitase = i,
                    fleetRequest = i,
                    buffer = i,
                    editableBuffer = true,
                    isDeposition = false
                )
            )
        }

        subjectUnderTest.listLocation.addAll(result)

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterLocation()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.FilterLocation(result), states[0])
        collect.cancel()
    }

    @Test
    fun `changeStatusOrderTest`() = runTest {
        val result : ArrayList<MonitoringModel> = ArrayList()

        for (i in 1 .. 3) {
            result.add(
                MonitoringModel(
                    subLocationId = i.toLong(),
                    locationName = "aa $i",
                    subLocationName = "bb $i",
                    fleetCount = i,
                    queueCount = i,
                    totalFleetCount = i,
                    totalQueueCount = i,
                    totalRitase = i,
                    fleetRequest = i,
                    buffer = i,
                    editableBuffer = true,
                    isDeposition = false
                )
            )
        }

        subjectUnderTest.listLocation.addAll(result)

        val collect = launch {
            subjectUnderTest.monitoringState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterLocation()
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(1, states.size)
        assertEquals(MonitoringState.FilterLocation(result), states[0])
        collect.cancel()
    }



}