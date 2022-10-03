package id.bluebird.vsm.feature.queue_fleet.search_fleet

import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_fleet.main.QueueFleetState
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class SearchFleetViewModelTest {

    private val _events = mutableListOf<SearchFleetState>()
    private lateinit var _vm: SearchFleetViewModel

    @BeforeEach
    fun setup() {
        _vm = SearchFleetViewModel()
    }

    @AfterEach
    fun shutDown() {
        _events.clear()
    }

    @Test
    fun `initList, pre listFleetItem, result UpdateFleetItems with list empty`() = runTest {
        // Pre
        val temp: MutableList<FleetItem> = mutableListOf()
        for (i in 1..10L) {
            temp.add(FleetItem(id = i))
        }
        _vm.setList(temp)

        // Execute
        val job = launch {
            _vm.searchState.toList(_events)
        }
        _vm.init(mutableListOf())
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(
            0,
            (_events.last() as SearchFleetState.UpdateFleetItems).list.size
        )
    }


    @Test
    fun `filter, given param is ABC, result UpdateFleetItems with list 1`() = runTest {
        // Pre
        val temp: MutableList<FleetItem> = mutableListOf()
        temp.add(FleetItem(id = 100, name = "ABC"))
        for (i in 1..10L) {
            temp.add(FleetItem(id = i, name = "$i"))
        }
        _vm.setList(temp)
        _vm.params.value = "ABC"

        // Execute
        val job = launch {
            _vm.searchState.toList(_events)
        }
        _vm.filter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(
            1,
            (_events.last() as SearchFleetState.UpdateFleetItems).list.size
        )
    }

    @Test
    fun `filter, given param is null, result UpdateFleetItems with list 10`() = runTest {
        // Pre
        val temp: MutableList<FleetItem> = mutableListOf()
        for (i in 1..10L) {
            temp.add(FleetItem(id = i, name = "$i"))
        }
        _vm.setList(temp)
        _vm.params.value = null

        // Execute
        val job = launch {
            _vm.searchState.toList(_events)
        }
        _vm.filter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(
            10,
            (_events.last() as SearchFleetState.UpdateFleetItems).list.size
        )
    }

    @Test
    fun `departFleet, given fleetItems, result UpdateFleetItems with list 10 and SuccessDepartFleet `() =
        runTest {
            // Pre
            val temp = ArrayList<FleetItem>()
            val fleetItemSample = FleetItem(id = 1, name = "aa", "2022-01-01")
            for (i in 1..10L) {
                temp.add(FleetItem(id = i, name = "$i"))
            }
            temp.add(fleetItemSample)
            _vm.setList(temp)

            // Execute
            val job = launch {
                _vm.searchState.toList(_events)
            }
            _vm.departFleet(fleetItemSample)
            advanceTimeBy(500)
            runCurrent()
            job.cancel()

            // Result
            Assertions.assertEquals(1, _events.size)
            Assertions.assertEquals(SearchFleetState.RequestDepartFleetItem(fleetItemSample), _events.first())
        }
}