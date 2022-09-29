package id.bluebird.vsm.feature.user_management.search_location

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.domain.interactor.GetLocations
import id.bluebird.vsm.domain.location.model.LocationResult
import id.bluebird.vsm.feature.user_management.TestCoroutineRule
import id.bluebird.vsm.feature.user_management.search_location.model.Location
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class SearchLocationViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var _vm: SearchLocationViewModel
    private val _getLocations: GetLocations = mockk()
    private val _events = mutableListOf<SearchLocationState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = SearchLocationViewModel(
            getLocations = _getLocations
        )
    }

    @AfterEach
    fun resetEvent() {
        _events.clear()
    }

    @Test
    fun `init, isFailed`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getLocations.invoke() } returns flow {
            throw NullPointerException(ERROR)
        }

        // Execute
        val job = launch(testDispatcher) {
            _vm.searchState.toList(_events)
        }
        _vm.init()
        runCurrent()
        job.cancel()

        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(SearchLocationState.OnProgressGetList, _events.first())
        Assertions.assertEquals(
            SearchLocationState.FailedGetList,
            _events.last()
        )
    }

    @Test
    fun `init, isEmpty`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getLocations.invoke() } returns flow {
            emit(
                LocationDomainState.Empty
            )
        }

        // Execute
        val job = launch(testDispatcher) {
            _vm.searchState.toList(_events)
        }
        _vm.init()
        runCurrent()
        job.cancel()

        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(SearchLocationState.OnProgressGetList, _events.first())
        Assertions.assertEquals(
            SearchLocationState.EmptyList,
            _events.last()
        )
    }

    @Test
    fun `init, isSuccess`() = runTest {
        //given
        val listLocationResult = ArrayList<LocationResult>()

        for (i in 1..2) {
            listLocationResult.add(
                LocationResult(
                    id = i.toLong() ,
                    locationName = "$i",
                    isActive = true,
                    codeArea = "$i" + "aa"
                )
            )
        }

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getLocations.invoke() } returns flow {
            emit(
                LocationDomainState.Success(
                    listLocationResult
                )
            )
        }

        // Execute
        val job = launch(testDispatcher) {
            _vm.searchState.toList(_events)
        }
        _vm.init()
        runCurrent()
        job.cancel()

        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(SearchLocationState.OnProgressGetList, _events.first())
        assert(_events.last() is SearchLocationState.Success)
    }

    @Test
    fun filterTest() = runTest {
        //given
        val tempList = ArrayList<Location>()
        tempList.add(
            Location(
                id = 1,
                name = "aa",
                isSelected = true
            )
        )
        _vm.setLocation(tempList)

        // Execute
         val job = launch(testDispatcher) {
             _vm.searchState.toList(_events)
         }
        _vm.filter()
        runCurrent()
        job.cancel()

        assert(_events.last() is SearchLocationState.Success)
    }

    @Test
    fun updateSelectedItemTest() = runTest {
        //given
        val tempLocation = Location(
            id = 1,
            name = "aa",
            isSelected = true
        )

        // Execute
        val job = launch(testDispatcher) {
            _vm.searchState.toList(_events)
        }
        _vm.updateSelectedItem(tempLocation, 0)
        runCurrent()
        job.cancel()

        assert(_events.last() is SearchLocationState.UpdateSelectedLocation)
    }

    @Test
    fun `chooseLocation, isSelectedLocation Not Null`() = runTest {
        val tempSelectedLocation = Location(
            id = 1,
            name = "aa",
            isSelected = true
        )
        _vm.selectedLocation.value = tempSelectedLocation

        // Execute
        val job = launch(testDispatcher) {
            _vm.searchState.toList(_events)
        }
        _vm.chooseLocation()
        runCurrent()
        job.cancel()

        assert(_events.last() is SearchLocationState.SetSelected)
    }

}