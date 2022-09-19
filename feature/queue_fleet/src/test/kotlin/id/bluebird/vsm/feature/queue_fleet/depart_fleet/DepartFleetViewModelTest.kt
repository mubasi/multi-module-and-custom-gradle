package id.bluebird.vsm.feature.queue_fleet.depart_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DepartFleetViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    private lateinit var _vm : DepartFleetViewModelTest
    private val _events = mutableListOf<DepartFleetState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = DepartFleetViewModelTest()
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