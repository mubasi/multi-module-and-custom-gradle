package id.bluebird.vsm.feature.select_location.search_mall_location

import id.bluebird.vsm.feature.select_location.TestCoroutineRule
import id.bluebird.vsm.feature.select_location.model.CacheParentModel
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.SubLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
internal class SearchMallLocationViewModelTest {

    private lateinit var subjectUnderTest: SearchMallLocationViewModel
    private val states = mutableListOf<SearchMallLocationState>()

    @BeforeEach
    fun setUp() {
        subjectUnderTest = SearchMallLocationViewModel()
    }

    @AfterEach
    fun resetState(){
        states.clear()
    }

    @Test
    fun `init, setListLocation`() = runTest {
        //given
        val location : ArrayList<LocationModel> = ArrayList()
        val listParent : ArrayList<CacheParentModel> = ArrayList()
        val listChild : ArrayList<SubLocation> = ArrayList()
        for (i in 1 .. 2) {
            val locationId = i.toLong()
            val name = "Mall $i"

            val tempChild : ArrayList<SubLocation> = ArrayList()

            tempChild.add(
                SubLocation(
                    id = (i + 1).toLong(),
                    name = "SubMall ",
                    locationId = locationId,
                    locationName = name,
                    type = 2
                )
            )

            location.add(
                LocationModel(
                    id = locationId,
                    name = name,
                    isExpanded = false,
                    type = 1,
                    list = tempChild
                )
            )
        }

        location.forEach { result ->
            val tempParent = CacheParentModel(
                id = result.id,
                name = result.name,
                isExpanded = result.isExpanded,
                type = result.type
            )
            listParent.add(tempParent)
            listChild.addAll(result.list)
        }

        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.init(parentList = listParent, childList = listChild)
        runCurrent()
        delay(1000)

        assertEquals(1, states.size)
        assertEquals(SearchMallLocationState.Init, states[0])

        collect.cancel()
    }


    @Test
    fun `expandOrCollapseParent, with given locationModel and position, emit onItemClick state with same given locationModel and position`() =
        runTest {
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
            assertEquals(SearchMallLocationState.OnItemClick(testItem, testPosition), states[0])
            collect.cancel()
        }

    @Test
    fun selectLocationTest()=
        runTest {
            //GIVEN
            val subLocation = SubLocation(11L, "subLocation name", 1L, "location name")

            val collect = launch {
                subjectUnderTest.state.toList(states)
            }

            //WHEN
            subjectUnderTest.selectLocation(subLocation)
            runCurrent()

            //THEN
            assertEquals(1, states.size)
            assertEquals(SearchMallLocationState.SelectLocation(
                locationId = subLocation.locationId,
                subLocationId = subLocation.id
            ), states[0])
            collect.cancel()
        }
}