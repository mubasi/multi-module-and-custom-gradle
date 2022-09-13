package id.bluebird.mall.feature_queue_fleet.depart_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.mall.feature_queue_fleet.TestCoroutineRule
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModel
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
class DepartFleetViewModel {

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
    fun cancelDepartTest() {
        // Execute
//        val job = launch {}
//
//        runCurrent()
//        job.cancel()
    }


}