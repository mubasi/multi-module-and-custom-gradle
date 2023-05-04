package id.bluebird.vsm.feature.airport_fleet.main

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.GetListFleetTerminalDepartState
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationStockCountDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.DispatchFleetAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetListFleetTerminal
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationStockCountDepart
import id.bluebird.vsm.domain.airport_assignment.model.EStatus
import id.bluebird.vsm.domain.airport_assignment.model.FleetItemDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.StockCountModel
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.domain.user.model.AssignmentLocationItem
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.feature.airport_fleet.utils.EmptyType
import id.bluebird.vsm.fleet_non_apsh.TestCoroutineRule
import id.bluebird.vsm.fleet_non_apsh.main.model.CountCache
import id.bluebird.vsm.fleet_non_apsh.main.model.LocationAssignmentModel
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
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class FleetNonApshViewModelTest {

    private val getUserAssignment: GetUserAssignment = mockk(relaxed = true)
    private val getCount: GetSubLocationStockCountDepart = mockk(relaxed = true)
    private val getStockBySubLocation: GetListFleetTerminal = mockk(relaxed = true)
    private val dispatchFleet: DispatchFleetAirport = mockk(relaxed = true)

    private lateinit var subjectUnderTest: FleetNonApshViewModel
    private val states = mutableListOf<FleetNonApshState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        mockkObject(UserUtils)
        mockkStatic(Hawk::class)
        subjectUnderTest = FleetNonApshViewModel(
            getUserAssignment, getCount, getStockBySubLocation, dispatchFleet
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `initTest when locationAssignment is null and get counter is true and condition error`() =
        runTest {
            //given
            val result = Throwable(message = error)

            every { UserUtils.getLocationId() } returns 1L
            every { getCount.invoke(any(), any(), any()) } returns flow {
                throw result
            }
            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.init(
                1L, "aa", perimeter = false, wings = false
            )
            runCurrent()
            delay(500)

            //then
            assertEquals(1L, subjectUnderTest.getIdSubLocation())
            assertEquals("aa", subjectUnderTest.getNameSubLocation())
            assertEquals(false, subjectUnderTest.isPerimeter.value)
            assertEquals(false, subjectUnderTest.isWings.value)
            assertEquals(1, states.size)
            assertEquals(
                FleetNonApshState.OnError(
                    result
                ), states[0]
            )
            collect.cancel()
        }

    @Test
    fun `initTest when locationAssignment is null and get counter is true and condition success`() =
        runTest {
            //given
            val locationAssigmentModel = LocationAssignmentModel(
                id = 1,
                name = "aa",
                isPerimeter = true,
                isWings = false
            )

            every { UserUtils.getLocationId() } returns 1L
            every { getCount.invoke(any(), any(), any()) } returns flow {
                emit(
                    GetSubLocationStockCountDepartState.Success(
                        StockCountModel(
                            1, 2, 3
                        )
                    )
                )
            }
            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.init(
                locationAssigmentModel.id,
                locationAssigmentModel.name,
                locationAssigmentModel.isPerimeter,
                locationAssigmentModel.isWings
            )
            runCurrent()
            delay(500)

            //then
            assertEquals(1, subjectUnderTest.getIdSubLocation())
            assertEquals("aa", subjectUnderTest.getNameSubLocation())
            assertEquals(true, subjectUnderTest.isPerimeter.value)
            assertEquals(false, subjectUnderTest.isWings.value)
            assertEquals(
                CountCache(
                    1, 2, 3, 0
                ), subjectUnderTest.counterLiveData.value
            )
            assertEquals(
                CountCache(
                    1, 2, 3, 0
                ), subjectUnderTest.getCounter()
            )
            assertEquals(1, states.size)
            assertEquals(FleetNonApshState.GetCountSuccess, states[0])
            collect.cancel()
        }

    @Test
    fun `get counterTest when refresh is false and condition success`() = runTest {
        //given

        every { UserUtils.getLocationId() } returns 1L
        every { getCount.invoke(any(), any(), any()) } returns flow {
            emit(
                GetSubLocationStockCountDepartState.Success(
                    StockCountModel(
                        1, 2, 3
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.runGetCounter(
            false
        )
        runCurrent()
        delay(500)

        //then
        assertEquals(
            CountCache(
                1, 2, 3, 0
            ), subjectUnderTest.counterLiveData.value
        )
        collect.cancel()
    }


    @Test
    fun `getFleetByLocationTest when get condition is error`() = runTest {
        //given
        val result = Throwable(message = error)
        every { getStockBySubLocation.invoke(any(), any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.getFleetByLocation()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.OnEmptyData, states[0]
        )
        collect.cancel()
    }

    @Test
    fun `getFleetByLocationTest when get condition is success and Empty with perimeter is true`() =
        runTest {
            //given
            val isPerimeter = true
            subjectUnderTest.setIsPerimeter(isPerimeter)

            every { getStockBySubLocation.invoke(any(), any(), any()) } returns flow {
                emit(
                    GetListFleetTerminalDepartState.EmptyResult
                )
            }
            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.getFleetByLocation()
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.OnEmptyData, states[0]
            )
            assertEquals(
                EmptyType.Perimeter, subjectUnderTest.emptyFleetList.value
            )
            collect.cancel()
        }

    @Test
    fun `getFleetByLocationTest when get condition is success and Empty with perimeter is false`() =
        runTest {
            //given
            val isPerimeter = false
            subjectUnderTest.setIsPerimeter(isPerimeter)

            every { getStockBySubLocation.invoke(any(), any(), any()) } returns flow {
                emit(
                    GetListFleetTerminalDepartState.EmptyResult
                )
            }
            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.getFleetByLocation()
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.OnEmptyData, states[0]
            )
            assertEquals(
                EmptyType.Terminal, subjectUnderTest.emptyFleetList.value
            )
            collect.cancel()
        }

    @Test
    fun `getFleetByLocationTest when get condition is success and not Empty`() = runTest {
        //given
        val listFleetItem: ArrayList<FleetItemDepartModel> = ArrayList()
        val linkedHashMap: LinkedHashMap<String, AssignmentCarCache> = LinkedHashMap()
        for (i in 0 until 1) {
            val tempTaxiNo = "aa $i"
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            listFleetItem.add(
                FleetItemDepartModel(
                    fleetId = i.toLong(),
                    taxiNo = tempTaxiNo,
                    status = EStatus.ARRIVED.name,
                    isTu = false,
                    sequence = i.toLong(),
                    createdAt = tempCreateAt
                )
            )
            linkedHashMap[tempTaxiNo] = AssignmentCarCache(
                fleetNumber = tempTaxiNo,
                date = tempCreateAt,
                dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                stockId = i.toLong(),
                isTU = false,
                sequence = i.toLong(),
                status = EStatus.ARRIVED.name
            )
        }
        every { getStockBySubLocation.invoke(any(), any(), any()) } returns flow {
            emit(
                GetListFleetTerminalDepartState.Success(
                    result = listFleetItem
                )
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.getFleetByLocation()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.Idle, states[0]
        )
//        assertEquals(
//            linkedHashMap, subjectUnderTest.getlinkedHashMap()
//        )
//        assertEquals(
//            linkedHashMap.values.toList(), subjectUnderTest.fleetLiveData.value
//        )
        collect.cancel()
    }

    @Test
    fun `getUserAssignmentTest when condition is error`() = runTest {
        //given
        val result = Throwable(message = error)

        every { UserUtils.getUserId() } returns 1L
        every { getUserAssignment.invoke(any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.getUserAssignment()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            2, states.size
        )
        assertEquals(
            FleetNonApshState.OnError(result), states[1]
        )
        assertEquals(
            FleetNonApshState.OnProgress, states[0]
        )
        collect.cancel()
    }

    @Test
    fun `getUserAssignmentTest when condition is success and list empty`() = runTest {
        //given
        val versionCode = 42L

        every { UserUtils.getUserId() } returns 1L
        every { getUserAssignment.invoke(any()) } returns flow {
            emit(
                GetUserAssignmentState.UserNotFound
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.getUserAssignment()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            2, states.size
        )
        assertEquals(
            FleetNonApshState.OnEmptyData, states[1]
        )
        assertEquals(
            FleetNonApshState.OnProgress, states[0]
        )
        collect.cancel()
    }


    @Test
    fun `getUserAssignmentTest when condition is success and length location is upper zero`() =
        runTest {
            //given
            val assignmentLocationItem = AssignmentLocationItem(
                subLocationId = 1L,
                subLocationName = "aa 1",
                isDeposition = false,
                locationId = 2L,
                isWings = false
            )

            every { UserUtils.getUserId() } returns 1L
            every { UserUtils.getLocationId() } returns 1L
            every { getUserAssignment.invoke(any()) } returns flow {
                emit(
                    GetUserAssignmentState.Success(
                        result = assignmentLocationItem
                    )
                )
            }
            every { getCount.invoke(any(), any(), any()) } returns flow {
                emit(
                    GetSubLocationStockCountDepartState.Success(
                        StockCountModel(
                            1, 2, 3,
                        )
                    )
                )
            }

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.getUserAssignment()
            runCurrent()
            delay(1000)

            //then
            assertEquals("aa 1", subjectUnderTest.getNameSubLocation())
            assertEquals(false, subjectUnderTest.isPerimeter.value)
            assertEquals(false, subjectUnderTest.isWings.value)
            assertEquals(2, states.size)
            assertEquals(
                FleetNonApshState.OnProgress, states[0]
            )
            assertEquals(FleetNonApshState.GetCountSuccess, states[1])
            assertEquals(
                CountCache(
                    1, 2, 3, 0
                ), subjectUnderTest.counterLiveData.value
            )
            collect.cancel()
        }

    @Test
    fun setLinkedHasMapTest() = runTest {
        //given
        val fleetItems: ArrayList<FleetItemDepartModel> = ArrayList()
        val tempLinkedMap: LinkedHashMap<String, AssignmentCarCache> = LinkedHashMap()
        for (i in 1..2) {
            val fleetNumber: String = "aa$i"
            fleetItems.add(
                FleetItemDepartModel(
                    fleetId = i.toLong(),
                    taxiNo = fleetNumber,
                    createdAt = "2022-07-18T07:54:14Z",
                    status = EStatus.ARRIVED.name,
                    isTu = false,
                    sequence = i.toLong()
                )
            )
            val tempCar = AssignmentCarCache(
                fleetNumber = fleetNumber,
                date = "2022-07-18T07:54:14Z",
                dateAfterConvert = "2022-07-18T07:54:14Z",
                stockId = i.toLong(),
                isSelected = false,
                status = EStatus.ARRIVED.name,
                isTU = false,
                sequence = i.toLong()
            )
            tempLinkedMap[fleetNumber] = tempCar
        }

        //when
        subjectUnderTest.setLinkedHasMap(fleetItems)

        //then
        assertEquals(
            tempLinkedMap,
            subjectUnderTest.getlinkedHashMap()
        )
    }

    @Test
    fun `intentToAddFleetPageTest when is perimeter is false and fleet user id is false`() =
        runTest {
            //given
            subjectUnderTest.setIsPerimeter(false)
            subjectUnderTest.setIsWing(false)
            subjectUnderTest.setSubLocation(1L)

            every { UserUtils.getFleetTypeId() } returns 1L

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.intentToAddFleetPage()
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.IntentToAddFleet(
                    isPerimeter = false,
                    subLocationId = 1L,
                    isWing = false
                ), states[0]
            )
            collect.cancel()
        }

    @Test
    fun `intentToAddFleetPageTest when is perimeter is true and fleet user id is false`() =
        runTest {
            //given
            subjectUnderTest.setIsPerimeter(true)
            subjectUnderTest.setIsWing(false)
            subjectUnderTest.setSubLocation(1L)

            every { UserUtils.getFleetTypeId() } returns 1L

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.intentToAddFleetPage()
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.IntentToAddFleet(
                    isPerimeter = false,
                    subLocationId = 1L,
                    isWing = false
                ), states[0]
            )
            collect.cancel()
        }

    @Test
    fun `intentToAddFleetPageTest when is perimeter is true and fleet user id is true`() = runTest {
        //given
        subjectUnderTest.setIsPerimeter(true)
        subjectUnderTest.setIsWing(false)
        subjectUnderTest.setSubLocation(1L)

        every { UserUtils.getFleetTypeId() } returns 2L

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.intentToAddFleetPage()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.IntentToAddFleet(
                isPerimeter = true,
                subLocationId = 1L,
                isWing = false
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun takePictureTest() = runTest {
        //given
        subjectUnderTest.setSubLocation(1L)
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.takePicture()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.TakePicture(
                1L
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun dialogRequestTest() = runTest {
        //given
        subjectUnderTest.setNameSubLocation("aa")
        subjectUnderTest.setSubLocation(1L)
        every { UserUtils.getLocationId() } returns 2L

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.dialogRequest()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.DialogRequest(
                requestToId = 2L,
                subLocationId = 1L,
                subLocationName = "aa"
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `updateRequestCountTest when count is under zero and fleetList is null`() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.updateRequestCount(-1L)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.OnEmptyData, states[0]
        )
        collect.cancel()
    }

    @Test
    fun `updateRequestCountTest when count is under zero and fleetList is not null`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val tempList: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..3) {
            tempList.add(
                AssignmentCarCache(
                    fleetNumber = "aa $i ",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isTU = false,
                    sequence = i.toLong(),
                    status = EStatus.ARRIVED.name
                )
            )
        }
        subjectUnderTest.setFleetList(tempList)

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.updateRequestCount(-1L)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            FleetNonApshState.Idle, states[0]
        )
        collect.cancel()
    }

    @Test
    fun `updateRequestCountTest when count is upper zero and fleetList is not null with counter is null`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            val tempList: ArrayList<AssignmentCarCache> = ArrayList()
            for (i in 1..3) {
                tempList.add(
                    AssignmentCarCache(
                        fleetNumber = "aa $i ",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isTU = false,
                        sequence = i.toLong(),
                        status = EStatus.ARRIVED.name
                    )
                )
            }
            subjectUnderTest.setFleetList(tempList)

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.updateRequestCount(2L)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.Idle, states[0]
            )
            assertEquals(
                CountCache(
                    0, 2, 0, 0
                ), subjectUnderTest.counterLiveData.value
            )
            collect.cancel()
        }

    @Test
    fun `updateRequestCountTest when count is upper zero and fleetList is not null with counter is not null`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            val tempList: ArrayList<AssignmentCarCache> = ArrayList()
            for (i in 1..3) {
                tempList.add(
                    AssignmentCarCache(
                        fleetNumber = "aa $i ",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isTU = false,
                        sequence = i.toLong(),
                        status = EStatus.ARRIVED.name
                    )
                )
            }
            val tempCounter = CountCache(
                5, 6, 7, 8
            )
            subjectUnderTest.setFleetList(tempList)
            subjectUnderTest.setCounter(tempCounter)

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.updateRequestCount(2L)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.Idle, states[0]
            )
            assertEquals(
                CountCache(
                    5, 2L, 7, 8
                ), subjectUnderTest.counterLiveData.value
            )
            collect.cancel()
        }

    @Test
    fun `setEmptyListTest when perimeter is true`() = runTest {
        //given
        subjectUnderTest.setIsPerimeter(true)

        //when
        subjectUnderTest.setEmptyList()
        runCurrent()
        delay(500)
        assertEquals(
            true, subjectUnderTest.isPerimeter.value
        )
        assertEquals(
            EmptyType.Perimeter, subjectUnderTest.emptyFleetList.value
        )
    }

    @Test
    fun `setEmptyListTest when perimeter is false`() = runTest {
        //given
        subjectUnderTest.setIsPerimeter(false)

        //when
        subjectUnderTest.setEmptyList()
        runCurrent()
        delay(500)
        assertEquals(
            false, subjectUnderTest.isPerimeter.value
        )
        assertEquals(
            EmptyType.Terminal, subjectUnderTest.emptyFleetList.value
        )
    }

    @Test
    fun `selectFleetTest when perimeter is true and status is otw`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        subjectUnderTest.setIsPerimeter(true)
        val assignmentCar = AssignmentCarCache(
            fleetNumber = "aa",
            date = tempCreateAt,
            dateAfterConvert = tempCreateAt.convertCreateAtValue(),
            stockId = 1L,
            isSelected = false,
            status = EStatus.OTW.name,
            isTU = false,
            sequence = 1L
        )

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.selectFleet(assignmentCar)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            1, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            EStatus.OTW.name, subjectUnderTest.statusFleetIsArrived.value
        )
        assertEquals(
            FleetNonApshState.DispatchCar(
                true
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `selectFleetTest when perimeter is true and status is arrive`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        subjectUnderTest.setIsPerimeter(true)
        val assignmentCar = AssignmentCarCache(
            fleetNumber = "aa",
            date = tempCreateAt,
            dateAfterConvert = tempCreateAt.convertCreateAtValue(),
            stockId = 1L,
            isSelected = false,
            status = EStatus.ARRIVED.name,
            isTU = false,
            sequence = 1L
        )
        val tempListAssigment: ArrayList<AssignmentCarCache> = arrayListOf(assignmentCar)

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.selectFleet(assignmentCar)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            1, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            EStatus.ARRIVED.name, subjectUnderTest.statusFleetIsArrived.value
        )
        assertEquals(
            FleetNonApshState.SendCar(
                tempListAssigment
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `selectFleetTest when perimeter is false and status is otw`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        subjectUnderTest.setIsPerimeter(false)
        val assignmentCar = AssignmentCarCache(
            fleetNumber = "aa",
            date = tempCreateAt,
            dateAfterConvert = tempCreateAt.convertCreateAtValue(),
            stockId = 1L,
            isSelected = false,
            status = EStatus.OTW.name,
            isTU = false,
            sequence = 1L
        )

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.selectFleet(assignmentCar)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            1, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            EStatus.OTW.name, subjectUnderTest.statusFleetIsArrived.value
        )
        assertEquals(
            FleetNonApshState.DispatchCar(
                true
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `selectFleetTest when perimeter is false and status is arrive`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        subjectUnderTest.setIsPerimeter(false)
        val assignmentCar = AssignmentCarCache(
            fleetNumber = "aa",
            date = tempCreateAt,
            dateAfterConvert = tempCreateAt.convertCreateAtValue(),
            stockId = 1L,
            isSelected = false,
            status = EStatus.ARRIVED.name,
            isTU = false,
            sequence = 1L
        )

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.selectFleet(assignmentCar)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            1, states.size
        )
        assertEquals(
            1, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            EStatus.ARRIVED.name, subjectUnderTest.statusFleetIsArrived.value
        )
        assertEquals(
            FleetNonApshState.DispatchCar(
                false
            ), states[0]
        )
        collect.cancel()
    }

    @Test
    fun `dispatchSend when isWithPassenger is null`() = runTest {
        //given

        //when
        subjectUnderTest.dispatchSend(null)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            0, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            false, subjectUnderTest.updateButtonFleetItem.value
        )
        assertEquals(
            "", subjectUnderTest.statusFleetIsArrived.value
        )
    }

    @Test
    fun `dispatchSendTest when isWithPassenger is false when isArrived is false and status is error`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            subjectUnderTest.setIsPerimeter(false)
            subjectUnderTest.setSubLocation(1L)
            val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
            for (i in 1..5) {
                listFleet.add(
                    AssignmentCarCache(
                        fleetNumber = "aa$i",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isSelected = true,
                        status = EStatus.OTW.name,
                        isTU = false,
                        sequence = 1L
                    )
                )
            }
            subjectUnderTest.setSelectedCarMap(listFleet)
            val errorCondition = Throwable(message = FleetNonApshViewModel.WRONG_LOCATION)
            every { UserUtils.getLocationId() } returns 2L
            every {
                dispatchFleet.invoke(any())
            } returns flow {
                throw errorCondition
            }

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.dispatchSend(false)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.OnError(
                    errorCondition
                ), states[0]
            )
            collect.cancel()
        }

    @Test
    fun `dispatchSendTest when isWithPassenger is false when isArrived is false and status is success and wrong location`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            subjectUnderTest.setIsPerimeter(false)
            subjectUnderTest.setSubLocation(1L)
            val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
            for (i in 1..5) {
                listFleet.add(
                    AssignmentCarCache(
                        fleetNumber = "aa$i",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isSelected = true,
                        status = EStatus.OTW.name,
                        isTU = false,
                        sequence = 1L
                    )
                )
            }
            subjectUnderTest.setSelectedCarMap(listFleet)
            every { UserUtils.getLocationId() } returns 2L
            every {
                dispatchFleet.invoke(any())
            } returns flow {
                emit(DispatchFleetAirportState.WrongDispatchLocation)
            }

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.dispatchSend(false)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assert(states[0] is FleetNonApshState.OnError)
            collect.cancel()
        }

    @Test
    fun `dispatchSendTest when isWithPassenger is false when isArrived is false and status is success and dispatchSuccessFleet`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            subjectUnderTest.setIsPerimeter(false)
            subjectUnderTest.setSubLocation(1L)
            val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
            for (i in 1..5) {
                listFleet.add(
                    AssignmentCarCache(
                        fleetNumber = "aa$i",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isSelected = true,
                        status = EStatus.OTW.name,
                        isTU = false,
                        sequence = 1L
                    )
                )
            }
            subjectUnderTest.setSelectedCarMap(listFleet)
            subjectUnderTest.setCounter(
                CountCache(
                    5, 6, 7
                )
            )
            every { UserUtils.getLocationId() } returns 2L
            every {
                dispatchFleet.invoke(any())
            } returns flow {
                emit(
                    DispatchFleetAirportState.SuccessDispatchFleet(
                        listFleet.size
                    )
                )
            }

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.dispatchSend(false)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.SuccessDispatchFleet(
                    5,
                    isNonTerminal = false,
                    isWithPassenger = false
                ), states[0]
            )
            assertEquals(
                CountCache(
                    0, 6, 12
                ), subjectUnderTest.getCounter()
            )
            assertEquals(
                0, subjectUnderTest.selectFleetCounter.value
            )
            assertEquals(
                false, subjectUnderTest.updateButtonFleetItem.value
            )
            collect.cancel()
        }

    @Test
    fun `dispatchSendTest when isWithPassenger is false when isArrived is false and status is success and arriveSuccess`() =
        runTest {
            //given
            val tempCreateAt = "2022-04-28T09:45:45.000000Z"
            subjectUnderTest.setIsPerimeter(false)
            subjectUnderTest.setSubLocation(1L)
            val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
            val resultArrived: HashMap<String, Long> = hashMapOf()
            for (i in 1..5) {
                listFleet.add(
                    AssignmentCarCache(
                        fleetNumber = "aa$i",
                        date = tempCreateAt,
                        dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                        stockId = i.toLong(),
                        isSelected = true,
                        status = EStatus.ARRIVED.name,
                        isTU = false,
                        sequence = 1L
                    )
                )
                resultArrived["aa$i"] = i.toLong()
            }
            subjectUnderTest.setSelectedCarMap(listFleet)
            subjectUnderTest.setCounter(
                CountCache(
                    5, 6, 7
                )
            )
            every { UserUtils.getLocationId() } returns 2L
            every {
                dispatchFleet.invoke(any())
            } returns flow {
                emit(
                    DispatchFleetAirportState.SuccessArrived(
                        resultArrived
                    )
                )
            }

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //when
            subjectUnderTest.dispatchSend(false)
            runCurrent()
            delay(500)

            //then
            assertEquals(
                1, states.size
            )
            assertEquals(
                FleetNonApshState.SuccessArrived(
                    5,
                    isStatusArrived = true,
                    isWithPassenger = false
                ), states[0]
            )
            assertEquals(
                CountCache(
                    10, 6, 7
                ), subjectUnderTest.getCounter()
            )
            assertEquals(
                0, subjectUnderTest.selectFleetCounter.value
            )
            assertEquals(
                false, subjectUnderTest.updateButtonFleetItem.value
            )
            collect.cancel()
        }

    @Test
    fun updateFleetSuccessArrivedTest() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        subjectUnderTest.setIsPerimeter(false)
        subjectUnderTest.setSubLocation(1L)
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        val resultArrived: HashMap<String, Long> = hashMapOf()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
            resultArrived["aa$i"] = i.toLong()
        }
        subjectUnderTest.setLinkedHashMap(listFleet)

        //when
        subjectUnderTest.updateFleetSuccessArrived(resultArrived)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            listFleet.size, subjectUnderTest.fleetLiveData.value?.size ?: 0
        )
    }

    @Test
    fun `updateStockFromListTest when statusUpdate is add`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val tempCounter = CountCache(
            5, 6, 7
        )
        subjectUnderTest.setCounter(
            tempCounter
        )
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        subjectUnderTest.setSelectedCarMap(listFleet)

        //when
        subjectUnderTest.updateStockFromList(FleetNonApshViewModel.StatusUpdate.ADD)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            CountCache(
                10, tempCounter.request, tempCounter.ritase
            ), subjectUnderTest.getCounter()
        )
    }


    @Test
    fun `updateStockFromListTest when statusUpdate is deficient`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val tempCounter = CountCache(
            5, 6, 7
        )
        subjectUnderTest.setCounter(
            tempCounter
        )
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        subjectUnderTest.setSelectedCarMap(listFleet)

        //when
        subjectUnderTest.updateStockFromList(FleetNonApshViewModel.StatusUpdate.DEFICIENT)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            CountCache(
                0, 6, 7
            ), subjectUnderTest.getCounter()
        )
    }


    @Test
    fun `updateRitaseFromListTest when statusUpdate is add`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val tempCounter = CountCache(
            5, 6, 7
        )
        subjectUnderTest.setCounter(
            tempCounter
        )
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        subjectUnderTest.setSelectedCarMap(listFleet)

        //when
        subjectUnderTest.updateRitaseFromList(FleetNonApshViewModel.StatusUpdate.ADD)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            CountCache(
                tempCounter.stock, tempCounter.request, 12
            ), subjectUnderTest.getCounter()
        )
    }

    @Test
    fun `updateRitaseFromListTest when statusUpdate is deficient`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val tempCounter = CountCache(
            5, 6, 7
        )
        subjectUnderTest.setCounter(
            tempCounter
        )
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        subjectUnderTest.setSelectedCarMap(listFleet)

        //when
        subjectUnderTest.updateRitaseFromList(FleetNonApshViewModel.StatusUpdate.DEFICIENT)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            CountCache(
                5, 6, 2
            ), subjectUnderTest.getCounter()
        )
    }

    @Test
    fun `setFleetSelectedTest when status is true`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = false,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        val resultTest = listFleet[0]
        subjectUnderTest.setSelectedCarMap(listFleet)
        subjectUnderTest.setFleetList(listFleet)

        //when
        subjectUnderTest.setFleetSelected(resultTest, true)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            resultTest, subjectUnderTest.getSelectedCarMap(resultTest.fleetNumber)
        )
        assertEquals(
            listFleet.size, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            resultTest.status, subjectUnderTest.statusFleetIsArrived.value
        )
    }

    @Test
    fun `setFleetSelectedTest when status is false`() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            listFleet.add(
                AssignmentCarCache(
                    fleetNumber = "aa$i",
                    date = tempCreateAt,
                    dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                    stockId = i.toLong(),
                    isSelected = true,
                    status = EStatus.OTW.name,
                    isTU = false,
                    sequence = 1L
                )
            )
        }
        val resultTest = listFleet[0]
        subjectUnderTest.setSelectedCarMap(listFleet)
        subjectUnderTest.setFleetList(listFleet)

        //when
        subjectUnderTest.setFleetSelected(resultTest, false)
        runCurrent()
        delay(500)

        //then
        assertEquals(
            (listFleet.size - 1), subjectUnderTest.getListSelectedCarMap().size
        )
        assertEquals(
            (listFleet.size - 1), subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            resultTest.status, subjectUnderTest.statusFleetIsArrived.value
        )
    }

    @Test
    fun `updateListFleetState when isShowFilterMessage is true`() = runTest {

        //when
        subjectUnderTest.updateListFleetState(true)
        runCurrent()

        //then
        assertEquals(
            EmptyType.FilterFleet, subjectUnderTest.emptyFleetList.value
        )
    }

    @Test
    fun `updateListFleetState when isShowFilterMessage is false and is perimeter true`() = runTest {
        //given
        subjectUnderTest.setIsPerimeter(true)

        //when
        subjectUnderTest.updateListFleetState(false)
        runCurrent()

        //then
        assertEquals(
            EmptyType.Perimeter, subjectUnderTest.emptyFleetList.value
        )
    }

    @Test
    fun `updateListFleetState when isShowFilterMessage is false and is perimeter false`() =
        runTest {
            //given
            subjectUnderTest.setIsPerimeter(false)

            //when
            subjectUnderTest.updateListFleetState(false)
            runCurrent()

            //then
            assertEquals(
                EmptyType.Terminal, subjectUnderTest.emptyFleetList.value
            )
        }

    @Test
    fun removeUpdateDataTest() = runTest {
        //given
        val tempCreateAt = "2022-04-28T09:45:45.000000Z"
        val listFleet: ArrayList<AssignmentCarCache> = ArrayList()
        val listFleetSelected: ArrayList<AssignmentCarCache> = ArrayList()
        for (i in 1..5) {
            val tempAssigment = AssignmentCarCache(
                fleetNumber = "aa$i",
                date = tempCreateAt,
                dateAfterConvert = tempCreateAt.convertCreateAtValue(),
                stockId = i.toLong(),
                isSelected = true,
                status = EStatus.OTW.name,
                isTU = false,
                sequence = 1L
            )
            listFleet.add(
                tempAssigment
            )

            if (i > 3) {
                listFleetSelected.add(
                    tempAssigment
                )
            }
        }

        subjectUnderTest.setLinkedHashMap(listFleet)
        subjectUnderTest.setSelectedCarMap(listFleetSelected)

        //when
        subjectUnderTest.removeUpdateData()
        runCurrent()
        delay(500)

        //then
        assertEquals(
            0, subjectUnderTest.fleetLiveData.value!!.size
        )
        assertEquals(
            0, subjectUnderTest.selectFleetCounter.value
        )
        assertEquals(
            false, subjectUnderTest.updateButtonFleetItem.value
        )
    }

}