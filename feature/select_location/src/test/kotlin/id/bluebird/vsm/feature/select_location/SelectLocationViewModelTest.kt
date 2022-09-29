package id.bluebird.vsm.feature.select_location

import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.domain.location.model.LocationsWithSub
import id.bluebird.vsm.domain.location.model.SubLocationResult
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.SubLocation
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
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
internal class SelectLocationViewModelTest {

    private val getLocationsWithSub: GetLocationsWithSub = mockk()
    private lateinit var subjectUnderTest: SelectLocationViewModel
    private val states = mutableListOf<SelectLocationState>()

    @BeforeEach
    fun setUp() {
        subjectUnderTest = SelectLocationViewModel(getLocationsWithSub)
    }

    @AfterEach
    fun resetStates() {
        states.clear()
    }

    @Test
    fun `init, when isFleetMenu true, result state changed to success with empty list`() = runTest {
        //GIVEN
        val isFleetMenu = true
        every { getLocationsWithSub.invoke() } returns flow {
            emit(
                GetLocationsWithSubState.Success(HashMap())
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //WHEN
        subjectUnderTest.init(isFleetMenu)
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(2, states.size)
        assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
        assertEquals(SelectLocationState.GetLocationSuccess(listOf()), states[1])

        collect.cancel()
    }

    @Test
    fun `init, when isFleetMenu true, result state changed to success with 1 list of 1 location with 2 subLocation`() = runTest {
        //GIVEN
        val isFleetMenu = true
        val locationId = 1L
        val locationName = "test name"
        val subLocationIds = arrayOf(11L, 12L)
        val subLocationName = "test subLocationName"

        every { getLocationsWithSub.invoke() } returns flow {
            emit(
                GetLocationsWithSubState.Success(
                    hashMapOf(
                        locationId to LocationsWithSub(
                            locationId,
                            locationName,
                            MutableList(subLocationIds.size) {
                                SubLocationResult(subLocationIds[it], subLocationName)
                            })
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //WHEN
        subjectUnderTest.init(isFleetMenu)
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(2, states.size)
        assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
        assertEquals(
            SelectLocationState.GetLocationSuccess(
                listOf(
                    LocationModel(
                        locationId, locationName, List(subLocationIds.size) {
                            SubLocation(
                                subLocationIds[it],
                                subLocationName,
                                locationId,
                                locationName
                            )
                        })
                )
            ), states[1]
        )

        collect.cancel()
    }

    @Test
    fun `expandOrCollapseParent, with given locationModel and position, emit onItemClick state with same given locationModel and position`() = runTest {
        //GIVEN
        val locationId = 1L
        val locationName = "location name"
        val testItem = LocationModel(
            locationId, locationName, listOf(
                SubLocation(11L, "subLocation name", locationId, locationName)
            )
        )
        val testPosition = 0
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //WHen
        subjectUnderTest.expandOrCollapseParent(testItem, testPosition)
        runCurrent()

        //THEN
        assertEquals(1, states.size)
        assertEquals(SelectLocationState.OnItemClick(testItem, testPosition), states[0])
        collect.cancel()

    }

    @Test
    fun `selectLocation, given test SubLocation, invoke updateLocationNav and set state to ToAssign`() = runTest {
        //GIVEN
        val isFleetMenu = false
        val subLocation = SubLocation(11L, "subLocation name", 1L, "location name")

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //WHEN
        subjectUnderTest.selectLocation(subLocation)
        runCurrent()

        //THEN
        assertEquals(1, states.size)
        assertEquals(SelectLocationState.ToAssign(isFleetMenu), states[0])
        collect.cancel()

    }

}