package id.bluebird.vsm.feature.queue_fleet.depart_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.domain.passenger.domain.interactor.CurrentQueueCases
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_fleet.add_fleet.AddFleetState
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
internal class DepartCarFleetViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    private lateinit var _vm : DepartFleetViewModel
    private val _events = mutableListOf<DepartFleetState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = DepartFleetViewModel()
    }

    @AfterEach
    fun resetEvent() {
        _events.clear()
    }

    @Test
    fun cancelDepartTest() = runTest {
        // Execute
        val job = launch {
            _vm.sharedDepartFleetState.toList(_events)
        }
        _vm.cancelDepart()
        runCurrent()
        job.cancel()


        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(DepartFleetState.CancelDepart, _events.last())
    }

    @Test
    fun departFleetTest() = runTest {
        val queueNumber = "aa"
        val tempStatusDepart = true
        val tempFleetItem = FleetItem(
            1, "aa", "bb"
        )
        _vm.setStatusDepart(tempStatusDepart)
        _vm.setFleetItem(tempFleetItem)
        // Execute
        val job = launch {
            _vm.sharedDepartFleetState.toList(_events)
        }
        _vm.departFleet(queueNumber)
        runCurrent()
        job.cancel()


        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(DepartFleetState.DepartFleet(tempFleetItem, tempStatusDepart, queueNumber), _events.last())
    }

    @Test
    fun showQueueListTest() = runTest {
        val queueNumber = "aa"
        val tempFleetItem = FleetItem(
            1, "aa", "bb"
        )
        val locationId = 1L
        val subLocationId = 11L
        _vm.setFleetItem(tempFleetItem)
        // Execute
        val job = launch {
            _vm.sharedDepartFleetState.toList(_events)
        }
        _vm.showQueueList(queueNumber, locationId, subLocationId)
        runCurrent()
        job.cancel()


        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(DepartFleetState.SelectQueueToDepart(tempFleetItem, queueNumber, locationId, subLocationId), _events.last())
    }


}