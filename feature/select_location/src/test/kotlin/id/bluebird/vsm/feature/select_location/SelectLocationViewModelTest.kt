package id.bluebird.vsm.feature.select_location

import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_location.GetListSublocationAirportState
import id.bluebird.vsm.domain.airport_location.domain.cases.GetListSublocationAirport
import id.bluebird.vsm.domain.airport_location.model.GetSubLocationByIdModel
import id.bluebird.vsm.domain.airport_location.model.SubLocationItemModel
import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.domain.location.model.LocationsWithSub
import id.bluebird.vsm.domain.location.model.SubLocationResult
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.domain.user.model.AssignmentLocationItem
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.LocationNavigation
import id.bluebird.vsm.feature.select_location.model.SubLocation
import id.bluebird.vsm.feature.select_location.model.SubLocationModelCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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
    private val getLocationAirport: GetListSublocationAirport = mockk()
    private lateinit var subjectUnderTest: SelectLocationViewModel
    private val states = mutableListOf<SelectLocationState>()

    @BeforeEach
    fun setUp() {
        mockkObject(UserUtils)
        subjectUnderTest = SelectLocationViewModel(getLocationsWithSub, getLocationAirport)
    }

    @AfterEach
    fun resetStates() {
        states.clear()
    }

    @Test
    fun `init, when isFleetMenu true and user is outlet, result state changed to success with empty list`() =
        runTest {
            //GIVEN
            val isFleetMenu = true
            every { UserUtils.getIsUserAirport() } returns false
            every { getLocationsWithSub.invoke() } returns flow {
                emit(
                    GetLocationsWithSubState.Success(listOf())
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
            assertEquals(3, states.size)
            assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
            assertEquals(SelectLocationState.UserOutlet, states[1])
            assertEquals(SelectLocationState.GetLocationSuccess(listOf()), states[2])

            collect.cancel()
        }


    @Test
    fun `init, when isFleetMenu true and user is outlet, result state changed to failed`() =
        runTest {
            //GIVEN
            val isFleetMenu = true
            val result = Throwable("error")
            every { UserUtils.getIsUserAirport() } returns false
            every { getLocationsWithSub.invoke() } returns flow {
                throw result
            }
            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //WHEN
            subjectUnderTest.init(isFleetMenu)
            runCurrent()
            delay(2000)

            //THEN
            assertEquals(3, states.size)
            assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
            assertEquals(SelectLocationState.UserOutlet, states[1])
            assertEquals(SelectLocationState.OnError(result), states[2])

            collect.cancel()
        }


    @Test
    fun `init, when isFleetMenu true and user is airport, result is success`() = runTest {
        //GIVEN
        val isFleetMenu = true
        every { UserUtils.getLocationId() } returns 1L
        every { UserUtils.getIsUserAirport() } returns true
        every { getLocationAirport.invoke(any(), any(), any()) } returns flow {
            emit(
                GetListSublocationAirportState.Success(
                    result = GetSubLocationByIdModel(
                        locationId = 1L,
                        locationName = "aa",
                        codeArea = "bb",
                        subLocationList = listOf(
                            SubLocationItemModel(
                                subLocationId = 2L,
                                subLocationName = "cc",
                                subLocationType = "dd",
                                isDeposition = false,
                                isWings = false
                            )
                        )
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
        assertEquals(3, states.size)
        assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
        assertEquals(SelectLocationState.UserAirport, states[1])
        assertEquals(
            SelectLocationState.GetSubLocationSuccess(
                listOf(SubLocationModelCache(
                    id = 2L,
                    name = "cc",
                    prefix = SelectLocationViewModel.EMPTY_STRING,
                    isPerimeter = false,
                    isWing = false
                ))
            ), states[2]
        )

        collect.cancel()
    }

    @Test
    fun `init, when isFleetMenu true and user is airport, result is empty result`() = runTest {
        //GIVEN
        val isFleetMenu = true
        every { UserUtils.getLocationId() } returns 1L
        every { UserUtils.getIsUserAirport() } returns true
        every { getLocationAirport.invoke(any(), any(), any()) } returns flow {
            emit(
                GetListSublocationAirportState.EmptyResult
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
        assertEquals(3, states.size)
        assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
        assertEquals(SelectLocationState.UserAirport, states[1])
        assertEquals(
            SelectLocationState.EmptyLocation, states[2]
        )

        collect.cancel()
    }

    @Test
    fun `init, when isFleetMenu true and user is airport, result is failed`() = runTest {
        //GIVEN
        val isFleetMenu = true
        val result = Throwable("error")
        every { UserUtils.getLocationId() } returns 1L
        every { UserUtils.getIsUserAirport() } returns true
        every { getLocationAirport.invoke(any(), any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //WHEN
        subjectUnderTest.init(isFleetMenu)
        runCurrent()
        delay(2000)

        //THEN
        assertEquals(3, states.size)
        assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
        assertEquals(SelectLocationState.UserAirport, states[1])
        assertEquals(
            SelectLocationState.OnError(
                result
            ), states[2]
        )

        collect.cancel()
    }


    @Test
    fun `init, when isFleetMenu true and user is outlet, result state changed to success with 1 list of 1 location with 2 subLocation`() =
        runTest {
            //GIVEN
            val isFleetMenu = true
            val locationId = 1L
            val locationName = "test name"
            val prefix = "prefix"
            val subLocationIds = arrayOf(11L, 12L)
            val subLocationName = "test subLocationName"
            val isDeposition = false
            val haveDeposition = false
            val idDeposition = 0L

            every { UserUtils.getIsUserAirport() } returns false
            every { getLocationsWithSub.invoke() } returns flow {
                emit(
                    GetLocationsWithSubState.Success(
                        listOf(
                            LocationsWithSub(
                                locationId,
                                locationName,
                                MutableList(subLocationIds.size) {
                                    SubLocationResult(
                                        subLocationIds[it],
                                        subLocationName,
                                        prefix,
                                        isDeposition,
                                        haveDeposition,
                                        depositionId = idDeposition
                                    )
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
            assertEquals(3, states.size)
            assertEquals(SelectLocationState.OnProgressGetLocations, states[0])
            assertEquals(SelectLocationState.UserOutlet, states[1])
            assertEquals(
                SelectLocationState.GetLocationSuccess(
                    listOf(
                        LocationModel(
                            locationId, locationName, List(subLocationIds.size) {
                                SubLocation(
                                    subLocationIds[it],
                                    subLocationName,
                                    locationId,
                                    locationName,
                                    prefix,
                                    haveDeposition,
                                    idDeposition,
                                    isDeposition,
                                )
                            })
                    )
                ), states[2]
            )

            collect.cancel()
        }

    @Test
    fun `expandOrCollapseParent, with given locationModel and position, emit onItemClick state with same given locationModel and position`() =
        runTest {
            //GIVEN
            val locationId = 1L
            val locationName = "location name"
            val prefix = "prefix"
            val isDepostion = false
            val haveDeposition = false
            val idDeposition = 0L
            val testItem = LocationModel(
                locationId, locationName, listOf(
                    SubLocation(
                        11L,
                        "subLocation name",
                        locationId,
                        locationName,
                        prefix,
                        haveDeposition,
                        idDeposition,
                        isDepostion
                    )
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
    fun `selectLocation, given test SubLocation, invoke updateLocationNav and set state to ToAssign`() =
        runTest {
            //GIVEN
            val isFleetMenu = false
            val haveDeposition = false
            val isDeposition = false
            val idDeposition = 0L
            val subLocation = SubLocation(
                11L,
                "subLocation name",
                1L,
                "location name",
                "prefix",
                haveDeposition,
                idDeposition,
                isDeposition
            )

            val locNav = LocationNavigation(
                locationId = subLocation.locationId,
                locationName = subLocation.locationName,
                subLocationId = subLocation.id,
                subLocationName = subLocation.name,
                prefix = subLocation.prefix,
                haveDeposition = subLocation.haveDeposition,
                idDeposition = subLocation.depositionId
            )

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //WHEN
            subjectUnderTest.selectLocation(subLocation)
            runCurrent()

            //THEN
            assertEquals(1, states.size)
            assertEquals(SelectLocationState.ToAssign(isFleetMenu), states[0])
            assertEquals(locNav, subjectUnderTest.locationNav)
            collect.cancel()

        }

    @Test
    fun `searchScreen, when user is outlet locationIsEmpty`() = runTest {
        val location: ArrayList<LocationModel> = ArrayList()
        subjectUnderTest.setValLocation(location)

        every { UserUtils.getIsUserAirport() } returns false

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.searchScreen()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.EmptyLocation, states[0])
    }

    @Test
    fun `searchScreen, when user is airport locationIsEmpty`() = runTest {
        val location: ArrayList<LocationModel> = ArrayList()
        subjectUnderTest.setValLocation(location)

        every { UserUtils.getIsUserAirport() } returns true

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.searchScreen()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.EmptyLocation, states[0])
    }

    @Test
    fun `searchScreen, when locationIsNotEmpty and isAirport is false`() = runTest {
        val location: ArrayList<LocationModel> = ArrayList()

        location.add(
            LocationModel(
                id = 1,
                name = "Location Name",
                list = listOf(),
                isExpanded = true,
                type = 1
            )
        )

        every {
            UserUtils.getIsUserAirport()
        } returns false

        subjectUnderTest.setValLocation(location)

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.searchScreen()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(
            SelectLocationState.SearchLocation(
                false
            ), states[0]
        )
    }

    @Test
    fun `setFromSearch, when isFleetMenuFalse and isAirport false`() = runTest {

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.setFromSearch(false)
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.ToAssignFromSearch(false), states[0])
    }

    @Test
    fun `setFromSearch, when isFleetMenuTrue and isAirport is false`() = runTest {

        subjectUnderTest.setValFleetMenu(true)

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.setFromSearch(false)
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.ToAssignFromSearch(true), states[0])
    }

    @Test
    fun `filterSearchTest when is Error`() = runTest {

        every { UserUtils.getIsUserAirport() } returns true
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.filterFleet()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.ErrorFilter, states[0])
    }


    @Test
    fun `filterSearchTest when user is airport is Not Error`() = runTest {
        val filteredlist: ArrayList<SubLocationModelCache> = ArrayList()

        filteredlist.add(
            SubLocationModelCache(
                id = 1,
                name = "Location Name",
                prefix = "LN",
                isPerimeter = false,
                isWing = false,
                type = 1
            )
        )

        subjectUnderTest.locationsAirport.addAll(filteredlist)
        every { UserUtils.getIsUserAirport() } returns true

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.filterFleet()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.FilterLocationAirport(filteredlist), states[0])
    }

    @Test
    fun `filterSearchTest when user is outlet is Not Error`() = runTest {
        val filteredlist: ArrayList<LocationModel> = ArrayList()

        filteredlist.add(
            LocationModel(
                id = 1,
                name = "Location Name",
                list = listOf(),
                isExpanded = true,
                type = 1
            )
        )

        subjectUnderTest._locations.addAll(filteredlist)
        every { UserUtils.getIsUserAirport() } returns false

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.filterFleet()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationState.FilterFleet(filteredlist), states[0])
    }

    @Test
    fun `clearSearchTest`() = runTest {
        val filteredlist: ArrayList<LocationModel> = ArrayList()

        filteredlist.add(
            LocationModel(
                id = 1,
                name = "Location Name",
                list = listOf(),
                isExpanded = true,
                type = 1
            )
        )

        subjectUnderTest._locations.addAll(filteredlist)
        every { UserUtils.getIsUserAirport() } returns false

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.clearSearch()
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(SelectLocationViewModel.EMPTY_STRING, subjectUnderTest.params.value)
        assertEquals(SelectLocationState.FilterFleet(filteredlist), states[0])
    }

    @Test
    fun `selectLocationAirportTest`() = runTest {

        val dataParam = SubLocationModelCache(
            id = 1L,
            name = "aa",
            prefix = "bb",
            isPerimeter = false,
            isWing = false
        )
        val result = LocationNavigation(
            subLocationId = dataParam.id,
            subLocationName = dataParam.name,
            isPerimeter = dataParam.isPerimeter,
            isWing = dataParam.isWing,
            prefix = dataParam.prefix
        )


        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        subjectUnderTest.selectLocationAirport(dataParam)
        runCurrent()
        collect.cancel()

        assertEquals(1, states.size)
        assertEquals(subjectUnderTest.locationNav, result)
        assertEquals(LocationNavigationTemporary.getLocationNav(), result)
        assertEquals(SelectLocationState.ToAssignAirport, states[0])


    }
}