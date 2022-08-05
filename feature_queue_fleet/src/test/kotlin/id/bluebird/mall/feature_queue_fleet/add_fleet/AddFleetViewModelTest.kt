package id.bluebird.mall.feature_queue_fleet.add_fleet

import id.bluebird.mall.domain_fleet.domain.cases.AddFleet
import id.bluebird.mall.domain_fleet.domain.cases.SearchFleet
import id.bluebird.mall.domain_pasenger.domain.cases.SearchWaitingQueue
import id.bluebird.mall.feature_queue_fleet.TestCoroutineRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class AddFleetViewModelTest {

    private lateinit var _vm: AddFleetViewModel
    private val _addFleet: AddFleet = mockk()
    private val _searchFleet: SearchFleet = mockk()
    private val _searchWaitingQueue: SearchWaitingQueue = mockk()

    @BeforeEach
    fun setup() {
        _vm = AddFleetViewModel(searchFleet = _searchFleet, addFleet = _addFleet, searchWaitingQueue = _searchWaitingQueue)
    }

    @Test
    fun `updateSelectedFleetNumber, given selectedFleetNumber , result selectedFleetNumber is empty`() =
        runTest {
            // Given
            val fleetNumber = "BB1212"
            _vm.selectedFleetNumber.value = fleetNumber

            // Pre
            Assertions.assertEquals(fleetNumber, _vm.selectedFleetNumber.value)

            // Execute
            _vm.updateSelectedFleetNumber(fleetNumber, 1)

            // Result
            Assertions.assertEquals("", _vm.selectedFleetNumber.value)
        }
}