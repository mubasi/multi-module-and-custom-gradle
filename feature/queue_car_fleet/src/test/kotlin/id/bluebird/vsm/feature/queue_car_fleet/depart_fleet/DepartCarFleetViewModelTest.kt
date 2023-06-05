package id.bluebird.vsm.feature.queue_car_fleet.depart_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.feature.queue_car_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
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

    private lateinit var _vm : DepartCarFleetViewModel
    private val _events = mutableListOf<DepartCarFleetState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = DepartCarFleetViewModel()
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
        Assertions.assertEquals(DepartCarFleetState.CancelDepartCar, _events.last())
    }

    @Test
    fun departFleetTest() = runTest {
        val queueNumber = "aa"
        val tempStatusDepart = true
        val tempCarFleetItem = CarFleetItem(
            1, "aa", "bb"
        )
        _vm.setStatusDepart(tempStatusDepart)
        _vm.setFleetItem(tempCarFleetItem)
        // Execute
        val job = launch {
            _vm.sharedDepartFleetState.toList(_events)
        }
        _vm.departFleet(queueNumber)
        runCurrent()
        job.cancel()


        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(DepartCarFleetState.DepartCarFleet(tempCarFleetItem, tempStatusDepart, queueNumber), _events.last())
    }

    @Test
    fun showQueueListTest() = runTest {
        val queueNumber = "aa"
        val tempCarFleetItem = CarFleetItem(
            1, "aa", "bb"
        )
        val locationId = 1L
        val subLocationId = 11L
        _vm.setFleetItem(tempCarFleetItem)
        // Execute
        val job = launch {
            _vm.sharedDepartFleetState.toList(_events)
        }
        _vm.showQueueList(queueNumber, locationId, subLocationId)
        runCurrent()
        job.cancel()


        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(DepartCarFleetState.SelectQueueToDepartCar(tempCarFleetItem, queueNumber, locationId, subLocationId), _events.last())
    }


}