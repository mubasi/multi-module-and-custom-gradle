package id.bluebird.vsm.feature.home.ritase_fleet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.FleetItemList
import id.bluebird.vsm.feature.home.model.UserInfo
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
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
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class RitaseFleetViewModelTest {

    @Rule
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var subjectUnderTest: RitaseFleetViewModel
    private val getFleet: GetListFleet = mockk(relaxed = true)
    private val states = mutableListOf<RitaseFleetState>()

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        mockkObject(LocationNavigationTemporary)
        subjectUnderTest = RitaseFleetViewModel(getFleet)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `init when location is not null and getFleet failed`() = runTest {
        //given
        val userId = 1L
        val locationId = 2L
        val subLocationId = 3L

        val mUserInfo = UserInfo(
            userId, locationId, subLocationId
        )

        val result = Throwable(message = "error")

        every { getFleet.invoke(any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.init(userId, locationId, subLocationId)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(2, states.size)
        assertEquals(3L, subjectUnderTest.mUserInfo.subLocationId)
        assertEquals(2L, subjectUnderTest.mUserInfo.locationId)
        assertEquals(1L, subjectUnderTest.mUserInfo.userId)
        assertEquals(mUserInfo, subjectUnderTest.mUserInfo)
        assertEquals(RitaseFleetState.ProsesListFleet, states[0])
        assertEquals(RitaseFleetState.FailedGetList(result), states[1])
        collect.cancel()
    }

    @Test
    fun `init when currentQueue is Not Null and location is not null with get fleet success and is not empty`() = runTest {
        val currentListItem : ArrayList<FleetItemResult> = ArrayList()
        val currentListResult : ArrayList<FleetItemList> = ArrayList()
        for (i in 1 .. 3) {
            val tempDate = "2023-02-15T02:15:00Z"
            currentListItem.add(
                FleetItemResult(
                    fleetId = i.toLong(),
                    fleetName = "aa $i",
                    arriveAt = tempDate
                )
            )
            currentListResult.add(
                FleetItemList(
                    id = i.toLong(),
                    name = "aa $i",
                    arriveAt = tempDate.convertCreateAtValue()
                )
            )
        }

        val userId = 1L
        val locationId = 2L
        val subLocationId = 3L

        val mUserInfo = UserInfo(
            userId, locationId, subLocationId
        )

        every { getFleet.invoke(any()) } returns flow {
            emit(
                GetListFleetState.Success(
                    list = currentListItem.toList()
                )
            )
        }
        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.init(userId, locationId, subLocationId)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(2, states.size)
        assertEquals(3L, subjectUnderTest.mUserInfo.subLocationId)
        assertEquals(2L, subjectUnderTest.mUserInfo.locationId)
        assertEquals(1L, subjectUnderTest.mUserInfo.userId)
        assertEquals(mUserInfo, subjectUnderTest.mUserInfo)
        assertEquals(RitaseFleetState.ProsesListFleet, states[0])
        assertEquals(RitaseFleetState.GetListSuccess(currentListResult), states[1])
        collect.cancel()
    }

    @Test
    fun `init when location is not null with get fleet success and is empty`() = runTest {
        val userId = 1L
        val locationId = 2L
        val subLocationId = 3L

        val mUserInfo = UserInfo(
            userId, locationId, subLocationId
        )

        every { getFleet.invoke(any()) } returns flow {
            emit(GetListFleetState.EmptyResult)
        }
        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.init(userId, locationId, subLocationId)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(2, states.size)
        assertEquals(3L, subjectUnderTest.mUserInfo.subLocationId)
        assertEquals(2L, subjectUnderTest.mUserInfo.locationId)
        assertEquals(1L, subjectUnderTest.mUserInfo.userId)
        assertEquals(mUserInfo, subjectUnderTest.mUserInfo)
        assertEquals(RitaseFleetState.ProsesListFleet, states[0])
        assertEquals(RitaseFleetState.GetListEmpty, states[1])
        collect.cancel()
    }

    @Test
    fun `updateSelectedFleetNumberTest when new position` () = runTest {
        val fleetNumber = "aa"

        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.updateSelectedFleetNumber(fleetNumber, 0)
        runCurrent()
        delay(500)

        assertEquals(1, states.size)
        assertEquals(RitaseFleetState.UpdateSelectPosition(
            -1, 0
        ), states[0])
        assertEquals(-1, subjectUnderTest.getValLastPosition())
        assertEquals(0, subjectUnderTest.getValNewtPosition())
        assertEquals(fleetNumber, subjectUnderTest.selectedFleetNumber.value)
        collect.cancel()
    }

    @Test
    fun `clearSelectedFleetTest`() = runTest {
        subjectUnderTest.clearSelected()
        assertEquals(null, subjectUnderTest.fleetItem.value)
        assertEquals("", subjectUnderTest.selectedFleetNumber.value)
    }

    @Test
    fun `saveFleet when selectedFleetNumber not selected`() = runTest {
        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        subjectUnderTest.saveFleet()
        runCurrent()
        delay(500)

        assertEquals(1, states.size)
        assertEquals(RitaseFleetState.FleetNotSelected, states[0])
        collect.cancel()
    }

    @Test
    fun `saveFleet when selectedFleetNumber selected`() = runTest {
        val fleetNumber = "aa"

        subjectUnderTest.selectedFleetNumber.value = fleetNumber

        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        subjectUnderTest.saveFleet()
        runCurrent()
        delay(500)

        assertEquals(1, states.size)
        assertEquals(RitaseFleetState.SuccessSaveFleet(
            fleetNumber
        ), states[0])
        collect.cancel()
    }

    @Test
    fun `filterFleet when list is Empty` () = runTest {
        subjectUnderTest.params.value = "aa"

        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(RitaseFleetState.FilterFleetFailed, states[0])
        collect.cancel()
    }

    @Test
    fun `filterFleet when list is not empty`() = runTest {
        val result : ArrayList<FleetItemList> = ArrayList()

        for (i in 1 .. 3) {
            result.add(
                FleetItemList(
                    id = i.toLong(),
                    name = "aa $i",
                    arriveAt = "bb $i",
                    isSelected = false,
                )
            )
        }

        subjectUnderTest.fleetItems.addAll(result)

        val collect = launch {
            subjectUnderTest.ritaseFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(RitaseFleetState.FilterFleet(result), states[0])
        collect.cancel()
    }

}