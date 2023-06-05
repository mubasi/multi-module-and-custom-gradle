package id.bluebird.vsm.feature.queue_car_fleet.add_by_camera

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.feature.queue_car_fleet.TestCoroutineRule
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
internal class AddCarFleetByCameraViewModelTest {
    companion object {
        private const val ERROR = "error"
    }

    private lateinit var _vm: AddCarFleetByCameraViewModel
    private val _events = mutableListOf<AddCarFleetByCameraState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = AddCarFleetByCameraViewModel()
    }

    @AfterEach
    fun resetEvent() {
        _events.clear()
    }

    @Test
    fun cancleScanTest() = runTest{

        // Execute
        val job = launch {
            _vm.addCarFleetByCameraState.toList(_events)
        }
        _vm.cancleScan()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddCarFleetByCameraState.CancleScan, _events.last())
    }

    @Test
    fun proseScanTest() = runTest{

        _vm.param.postValue("aa")

        // Execute
        val job = launch {
            _vm.addCarFleetByCameraState.toList(_events)
        }
        _vm.proseScan()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddCarFleetByCameraState.ProsesScan("aa"), _events.last())
    }


    @Test
    fun repeatTakePictureTest() = runTest{

        // Execute
        val job = launch {
            _vm.addCarFleetByCameraState.toList(_events)
        }
        _vm.repeatTakePicture()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddCarFleetByCameraState.RepeatTakePicture, _events.last())
    }
}