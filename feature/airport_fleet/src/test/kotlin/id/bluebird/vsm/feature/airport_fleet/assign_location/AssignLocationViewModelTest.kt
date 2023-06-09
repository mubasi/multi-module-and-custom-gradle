package id.bluebird.vsm.feature.airport_fleet.assign_location

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.AssignFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.RitaseFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AssignFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RitaseFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetTerminalAirportModel
import id.bluebird.vsm.domain.airport_assignment.model.EStatus
import id.bluebird.vsm.domain.airport_assignment.model.GetSubLocationAirportModel
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignLocationModel
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.fleet_non_apsh.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
internal class AssignLocationViewModelTest {

    private val getSubLocationAssigment : GetSubLocationAirport = mockk(relaxed = true)
    private val ritaseFleetTerminal : RitaseFleetTerminalAirport = mockk(relaxed = true)
    private val assignFleetTerminal : AssignFleetTerminalAirport = mockk(relaxed = true)
    private lateinit var subjectUnderTest: AssignLocationViewModel
    private var states = mutableListOf<AssignLocationState>()
    private val error = "error"

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        subjectUnderTest = AssignLocationViewModel(
            getSubLocationAssigment, ritaseFleetTerminal, assignFleetTerminal
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun setupVersionCodeTest() = runTest {
        //given
        val versionCode = 1

        //when
        subjectUnderTest.setupVersionCode(versionCode)

        //then
        assertEquals(
            versionCode.toLong(),
            subjectUnderTest.getVersionCode()
        )
    }

    @Test
    fun onBackTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.onBack()
        runCurrent()
        delay(500)

        //then
        assertEquals(1, states.size)
        assertEquals(
            AssignLocationState.Back,
            states[0]
        )
        collect.cancel()
    }

    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is Null`() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(1, states.size)
        assertEquals(
            AssignLocationState.LocationIsNoSelected,
            states[0]
        )
        collect.cancel()
    }

    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is not Null and fleet is empty`() = runTest {
        //given
        subjectUnderTest.setSelectedLocation(
            AssignLocationModel(
                id = 1L,
                name = "aa",
                request = 2L,
                checked = true,
                isWithPassenger = false,
                isNonTerminal = true
            )
        )
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(1, states.size)
        assertEquals(
            AssignLocationState.SelectedCarIsEmpty,
            states[0]
        )
        collect.cancel()
    }

    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is not Null and fleet is not empty with perimeter is true and result is error`() = runTest {
        //given
        subjectUnderTest.setSelectedLocation(
            AssignLocationModel(
                id = 1L,
                name = "aa",
                request = 2L,
                checked = true,
                isWithPassenger = false,
                isNonTerminal = true
            )
        )
        val tempFleets : ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1 .. 5) {
            val tempTaxiNo = "aa $i"
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            tempFleets.add(
                AssignmentCarCache(
                    fleetNumber = tempTaxiNo,
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isTU = false,
                    sequence = i.toLong(),
                    status = EStatus.OTW.name
                )
            )
        }
        subjectUnderTest.setListFleet(tempFleets)
        subjectUnderTest.setIsPerimeter(true)
        val result = Throwable(message = error)
        every { assignFleetTerminal.invoke(any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.SendFleetOnProgress,
            states[0]
        )
        assertEquals(
            AssignLocationState.OnError(result),
            states[1]
        )
        collect.cancel()
    }

    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is not Null and fleet is not empty with perimeter is true and result is success`() = runTest {
        //given
        val tempLocationSelected = AssignLocationModel(
                id = 1L,
                name = "aa",
                request = 2L,
                checked = true,
                isWithPassenger = false,
                isNonTerminal = true
            )
        subjectUnderTest.setSelectedLocation(
            tempLocationSelected
        )
        val tempFleets : ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1 .. 5) {
            val tempTaxiNo = "aa $i"
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            tempFleets.add(
                AssignmentCarCache(
                    fleetNumber = tempTaxiNo,
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isTU = false,
                    sequence = i.toLong(),
                    status = EStatus.OTW.name
                )
            )
        }
        subjectUnderTest.setListFleet(tempFleets)
        subjectUnderTest.setIsPerimeter(true)
        every { assignFleetTerminal.invoke(any()) } returns flow {
            emit(
                AssignFleetTerminalAirportState.Success(
                    AssignFleetTerminalAirportModel(
                        "aa",
                        tempFleets.size.toLong()
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.SendFleetOnProgress,
            states[0]
        )
        assertEquals(
            AssignLocationState.SendCarSuccess(
                tempFleets.size,
                tempFleets.size,
                tempLocationSelected
            ),
            states[1]
        )
        collect.cancel()
    }


    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is not Null and fleet is not empty with perimeter is false and result is error`() = runTest {
        //given
        subjectUnderTest.setSelectedLocation(
            AssignLocationModel(
                id = 1L,
                name = "aa",
                request = 2L,
                checked = true,
                isWithPassenger = false,
                isNonTerminal = true
            )
        )
        val tempFleets : ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1 .. 5) {
            val tempTaxiNo = "aa $i"
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            tempFleets.add(
                AssignmentCarCache(
                    fleetNumber = tempTaxiNo,
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isTU = false,
                    sequence = i.toLong(),
                    status = EStatus.OTW.name
                )
            )
        }
        subjectUnderTest.setListFleet(tempFleets)
        subjectUnderTest.setIsPerimeter(false)
        val result = Throwable(message = error)
        every { ritaseFleetTerminal.invoke(any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.SendFleetOnProgress,
            states[0]
        )
        assertEquals(
            AssignLocationState.OnError(result),
            states[1]
        )
        collect.cancel()
    }

    @Test
    fun `sendFleetsFromPerimeter when selectedLocation is not Null and fleet is not empty with perimeter is false and result is success`() = runTest {
        //given
        val tempLocationSelected = AssignLocationModel(
            id = 1L,
            name = "aa",
            request = 2L,
            checked = true,
            isWithPassenger = false,
            isNonTerminal = true
        )
        subjectUnderTest.setSelectedLocation(
            tempLocationSelected
        )
        val tempFleets : ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1 .. 5) {
            val tempTaxiNo = "aa $i"
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            tempFleets.add(
                AssignmentCarCache(
                    fleetNumber = tempTaxiNo,
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isTU = false,
                    sequence = i.toLong(),
                    status = EStatus.OTW.name
                )
            )
        }
        subjectUnderTest.setListFleet(tempFleets)
        subjectUnderTest.setIsPerimeter(false)
        every { ritaseFleetTerminal.invoke(any()) } returns flow {
            emit(
                RitaseFleetTerminalAirportState.Success(
                    AddStockDepartModel(
                        "aa",
                        stockType = "bb",
                        stockId = 1L,
                        createdAt = "cc",
                        arrivedItem = ArrayList(),
                        taxiList = ArrayList(),
                        currentTuSpace = 1L
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.sendFleetsFromPerimeter()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.SendFleetOnProgress,
            states[0]
        )
        assertEquals(
            AssignLocationState.SendCarFromAirport(
                tempFleets.size,
                tempFleets.size,
                false,
                isStatusArrived = false
            ),
            states[1]
        )
        collect.cancel()
    }

    @Test
    fun `updateSelectedLocationTest`() = runTest {
        //given
        val tempLocationSelected = AssignLocationModel(
            id = 1L,
            name = "aa",
            request = 2L,
            checked = true,
            isWithPassenger = false,
            isNonTerminal = true
        )

        //when
        subjectUnderTest.updateSelectedLocation(tempLocationSelected)

        //then
        assertEquals(
            tempLocationSelected,
            subjectUnderTest.selectedLocation.value
        )
    }

    @Test
    fun `updateLocationToAssignTest`() = runTest {
        //given
        val tempLocationSelected = AssignLocationModel(
            id = 1L,
            name = "aa",
            request = 2L,
            checked = true,
            isWithPassenger = false,
            isNonTerminal = true
        )

        //when
        subjectUnderTest.updateLocationToAssign(tempLocationSelected)

        //then
        assertEquals(
            tempLocationSelected,
            subjectUnderTest.selectedLocation.value
        )
    }

    @Test
    fun `getAssignLocationTest when condition is error`() = runTest {
        //given
        subjectUnderTest.setupVersionCode(1)
        val result = Throwable(error)
        every { UserUtils.getLocationId() } returns 1L
        every { getSubLocationAssigment.invoke(any(), any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.getAssignLocation()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.GetListProsess,
            states[0]
        )
        assertEquals(
            AssignLocationState.OnError(
                result
            ),
            states[1]
        )
        collect.cancel()
    }

    @Test
    fun `getAssignLocationTest when condition is success`() = runTest {
        //given
        subjectUnderTest.setupVersionCode(1)
        every { UserUtils.getLocationId() } returns 1L
        every { getSubLocationAssigment.invoke(any(), any(), any()) } returns flow {
            emit(
                GetSubLocationAirportState.Success(
                    GetSubLocationAirportModel(
                        locationName = "aa",
                        locationId  = 1L,
                        countSubLocationItem = ArrayList()
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //when
        subjectUnderTest.getAssignLocation()
        runCurrent()
        delay(500)

        //then
        assertEquals(2, states.size)
        assertEquals(
            AssignLocationState.GetListProsess,
            states[0]
        )
        assert(
            states[1] is AssignLocationState.GetListSuccess
        )
        collect.cancel()
    }

}