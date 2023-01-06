package id.bluebird.vsm.feature.queue_fleet.main

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.GetCountState
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.DepartFleet
import id.bluebird.vsm.domain.fleet.domain.cases.GetCount
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.CountResult
import id.bluebird.vsm.domain.fleet.model.FleetDepartResult
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_fleet.model.CountCache
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import id.bluebird.vsm.feature.queue_fleet.model.UserInfo
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import id.bluebird.vsm.feature.select_location.model.LocationNavigation
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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
internal class QueueFleetViewModelTest {

    companion object {

        private const val ERROR = "error"
    }
    //    @Rule
//    private val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var _vm: QueueFleetViewModel
    private val _getCount: GetCount = mockk(relaxed = true)
    private val _getUserId: GetUserId = mockk(relaxed = true)
    private val _getFleetList: GetListFleet = mockk(relaxed = true)
    private val _departFleet: DepartFleet = mockk(relaxed = true)
    private val _events = mutableListOf<QueueFleetState>()
    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        mockkObject(LocationNavigationTemporary)
        _vm = QueueFleetViewModel(
            _getCount,
            _getUserId,
            _getFleetList,
            _departFleet,
        )
    }
    @AfterEach
    fun tearDown() {
        _events.clear()
    }
    @Test
    fun `getUserById, isSuccess`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(1) } returns flow {
            emit(
                GetUserByIdState.Success(
                    CreateUserResult(
                        name = "aa",
                        username = "bb",
                        locationId = 10,
                        locationName = "Location",
                        subLocationsId = mutableListOf(1, 2, 3, 4),
                        subLocationName = "Sub Location"
                    )
                )
            )
        }
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.runTestGetUserById()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events.first())
        Assertions.assertEquals(
            QueueFleetState.GetUserInfoSuccess,
            _events.last()
        )
    }
    @Test
    fun `getUserById, isFailed`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.runTestGetUserById()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events.first())
        Assertions.assertEquals(
            QueueFleetState.FailedGetUser(ERROR),
            _events.last()
        )
    }
    @Test
    fun `getCounter, given userInfo, Result success`() = runTest {
        // Given
        _vm.setUserInfo(UserInfo(10))
        // Mock
        every { _getCount.invoke(any(), any()) } returns flow {
            emit(GetCountState.Success(CountResult(10, 11, 12)))
        }
        // Execute
        _vm.getCounter()
        runCurrent()
        // Result
        Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
        Assertions.assertEquals(11, _vm.counterLiveData.value!!.ritase)
        Assertions.assertEquals(12, _vm.counterLiveData.value!!.request)
    }
    @Test
    fun `getCounter, given userInfo, Result throw error`() = runTest {
        // Given
        _vm.setUserInfo(UserInfo(10))
        // Mock
        every { _getCount.invoke(any(), any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.getCounter()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(
            QueueFleetState.FailedGetCounter(ERROR),
            _events.last()
        )
    }
    @Test
    fun `updateRequestCount, given new requestCount 10, result countCache request is 5`() =
        runTest {
            // Execute
            _vm.updateRequestCount(10)
            // Result
            Assertions.assertEquals(10, _vm.counterLiveData.value!!.request)
        }
    @Test
    fun `showRequestFleet, result state is showRequestFleet with subLocationId 11`() = runTest {
        // Given
        _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 11))
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.showRequestFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(QueueFleetState.ShowRequestFleet(11), _events.last())
    }
    @Test
    fun `idleState, result state is Idle`() = runTest {
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.stateIdle()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(QueueFleetState.Idle, _events.last())
    }
    @Test
    fun `searchFleet, result state is SearchFleet with subLocationId & FleetItems`() = runTest {
        // Pre
        _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 11))
        _vm.setFleetItems(mutableListOf())
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.searchFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(11, (_events.last() as QueueFleetState.SearchFleet).subLocationId)
        Assertions.assertEquals(
            emptyList<FleetItem>(),
            (_events.last() as QueueFleetState.SearchFleet).list
        )
        assert(_events.last() is QueueFleetState.SearchFleet)
    }
    @Test
    fun `getListFleet, given subLocationId, condition don't hit api, result QueueFleetState Success`() =
        runTest {
            // Result
            _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 12))
            val fleetItems: MutableList<FleetItem> = mutableListOf()
            fleetItems.add(FleetItem())
            _vm.setFleetItems(fleetItems)
            // Execute
            val job = launch {
                _vm.queueFleetState.toList(_events)
            }
            _vm.getFleetList()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(2, _events.size)
            assert(_events.last() is QueueFleetState.GetListSuccess)
            Assertions.assertEquals(
                1,
                (_events.last() as QueueFleetState.GetListSuccess).list.size
            )
        }
    @Test
    fun `getListFleet, given subLocationId, result QueueFleetState Success`() = runTest {
        // Result
        _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 12))
        val list = mutableListOf<FleetItemResult>()
        // Mock
        every {
            _getFleetList.invoke(any())
        } returns flow {
            for (i in 1..9L) {
                list.add(
                    FleetItemResult(
                        fleetId = i,
                        fleetName = "BB1${i}1",
                        arriveAt = "2022-07-19T0${i}:03:13Z"
                    )
                )
            }
            emit(GetListFleetState.Success(list))
        }
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.getFleetList()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueFleetState.GetListSuccess)
        Assertions.assertEquals(
            9,
            (_events.last() as QueueFleetState.GetListSuccess).list.size
        )
    }
    @Test
    fun `getListFleet, given subLocationId, result QueueFleetState Empty`() = runTest {
        // Result
        _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 12))
        // Mock
        every {
            _getFleetList.invoke(any())
        } returns flow {
            emit(GetListFleetState.EmptyResult)
        }
        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.getFleetList()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueFleetState.GetListEmpty, _events.last())
    }
    @Test
    fun `getListFleet, given subLocationId, throw exception, result QueueFleetState FailedGetList`() =
        runTest {
            // Result
            _vm.setUserInfo(UserInfo(userId = 10, subLocationId = 12))
            val exception = NullPointerException()
            // Mock
            every {
                _getFleetList.invoke(any())
            } returns flow {
                throw exception
            }
            // Execute
            val job = launch {
                _vm.queueFleetState.toList(_events)
            }
            _vm.getFleetList()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(3, _events.size)
            Assertions.assertEquals(QueueFleetState.ProgressGetFleetList, _events.first())
            Assertions.assertEquals(QueueFleetState.FailedGetList(exception), _events[1])
            Assertions.assertEquals(QueueFleetState.GetListEmpty, _events.last())
        }
    @Test
    fun `addSuccess, pre CountCache stock is 10, condition fleetNumber is notBlank, result stock change to 11`() =
        runTest {
            // Pre
            _vm.setCountCache(CountCache(stock = 10))
            Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
            // Execute
            val job = launch {
                _vm.queueFleetState.toList(_events)
            }
            _vm.addSuccess(FleetItem(id = 1, name = "", arriveAt = ""))
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(11, _vm.counterLiveData.value!!.stock)
            assert(_events.last() is QueueFleetState.AddFleetSuccess)
        }
    @Test
    fun `addSuccess, pre CountCache stock is 10, condition fleetNumber isBlank, result stock isNotChange`() =
        runTest {
            // Pre
            _vm.setCountCache(CountCache(stock = 10))
            Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
            // Execute
            _vm.addSuccess(null)
            // Result
            Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
        }
    @Test
    fun `init, when user is not officer and LocationNav not available, emit toSelectLocation`() =
        runTest {
            //GIVEN
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns false
            val collect = launch {
                _vm.queueFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            delay(500)
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueFleetState.ProgressHolder, _events[0])
            Assertions.assertEquals(QueueFleetState.ToSelectLocation, _events[1])
            collect.cancel()
        }
    @Test
    fun `init, when user is not officer and locationNav available, emit getUserSuccess`() =
        runTest {
            //GIVEN
            val userResult =
                CreateUserResult(
                    1L,
                    "name",
                    "username",
                    1L,
                    1L,
                    "locationName",
                    listOf(11L),
                    "subLocationName"
                )
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns true
            every { UserUtils.isUserOfficer() } returns false
            every { UserUtils.getUserId() } returns 1L
            every { LocationNavigationTemporary.getLocationNav() } returns LocationNavigation(
                1L,
                11L,
                "locationName",
                "subLocationName"
            )
            every { _getUserId.invoke(1L) } returns flow {
                emit(GetUserByIdState.Success(userResult))
            }
            val collect = launch {
                _vm.queueFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueFleetState.GetUserInfoSuccess, _events[1])
            Assertions.assertEquals(
                UserInfo(1L, 1L, 11L),
                _vm.valUserInfo()
            )

            collect.cancel()
        }
    @Test
    fun `init, when user is officer and locationNav not available, emit getUserSuccess`() =
        runTest {
            //GIVEN
            val userResult =
                CreateUserResult(
                    1L,
                    "name",
                    "username",
                    5L,
                    1L,
                    "locationName",
                    listOf(11L),
                    "subLocationName"
                )
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns true
            every { UserUtils.getUserId() } returns 1L
            every { _getUserId.invoke(1L) } returns flow {
                emit(GetUserByIdState.Success(userResult))
            }
            val collect = launch {
                _vm.queueFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueFleetState.GetUserInfoSuccess, _events[1])
            Assertions.assertEquals(
                UserInfo(1L, 1L, 11L),
                _vm.valUserInfo()
            )

            collect.cancel()
        }
    @Test
    fun `init, when user is officer and locationNav available, emit getUserSuccess`() = runTest {
        //GIVEN
        val userResult =
            CreateUserResult(
                1L,
                "name",
                "username",
                5L,
                1L,
                "locationName",
                listOf(11L),
                "subLocationName"
            )
        every { LocationNavigationTemporary.isLocationNavAvailable() } returns true
        every { UserUtils.isUserOfficer() } returns true
        every { UserUtils.getUserId() } returns 1L
        every { _getUserId.invoke(1L) } returns flow {
            emit(GetUserByIdState.Success(userResult))
        }
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        //WHEN
        _vm.init()
        runCurrent()
        //THEN
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events[0])
        Assertions.assertEquals(QueueFleetState.GetUserInfoSuccess, _events[1])
        Assertions.assertEquals(
            UserInfo(1L, 1L, 11L),
            _vm.valUserInfo()
        )

        collect.cancel()
    }
    @Test
    fun `init, when user is officer and locationNav not available and failed to getUser, emit failedGetUser`() =
        runTest {
            //GIVEN
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns true
            every { UserUtils.getUserId() } returns 1L
            every { _getUserId.invoke(1L) } returns flow {
                throw NullPointerException(ERROR)
            }
            val collect = launch {
                _vm.queueFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueFleetState.FailedGetUser(ERROR), _events[1])
            Assertions.assertEquals(
                UserInfo(),
                _vm.valUserInfo()
            )

            collect.cancel()
        }
    @Test
    fun initLocationTest() = runTest {
        _vm.initLocation(1, 2)

        Assertions.assertEquals(1, _vm.mUserInfo.locationId)
        Assertions.assertEquals(2, _vm.mUserInfo.subLocationId)
    }
    @Test
    fun `initLocationTest, when location and sublocation smaller 0`() = runTest {
        _vm.initLocation(-1, -1)

        Assertions.assertEquals(-1, _vm.mUserInfo.locationId)
        Assertions.assertEquals(-1, _vm.mUserInfo.subLocationId)
    }
    @Test
    fun `departFleet, when with passenger and queueIsBlank`() = runTest {
        val withPassenger = true
        val queueId = ""
        val fleetItem = FleetItem(
            id = 1,
            name = "aa",
            arriveAt = "bb"
        )
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.departFleet(fleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueFleetState.RecordRitaseToDepart)
        collect.cancel()
    }
    @Test
    fun `departFleet, when with passenger and queueisNotBlank is Error`() = runTest {
        val withPassenger = false
        val queueId = "aa"
        val fleetItem = FleetItem(
            id = 1,
            name = "aa",
            arriveAt = "bb"
        )

        every { _departFleet.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.departFleet(fleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueFleetState.FailedDepart)
        collect.cancel()
    }
    @Test
    fun `departFleet, when with passenger and queueisNotBlank is Success`() = runTest {
        val withPassenger = false
        val queueId = "aa"
        val fleetItem = FleetItem(
            id = 1,
            name = "aa",
            arriveAt = "bb"
        )

        every { _departFleet.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(
                DepartFleetState.Success(
                    FleetDepartResult(
                        taxiNo = "aa",
                        message = "bb",
                        stockType = "cc",
                        stockId = "dd",
                        createdAt = "ff"
                    )
                )
            )
        }
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.departFleet(fleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueFleetState.SuccessDepartFleet)
        collect.cancel()
    }
    @Test
    fun removeFleetTest() = runTest {
        val listFleet = ArrayList<FleetItem>()

        listFleet.add(
            FleetItem(
                1, "aa", "bb"
            )
        )
        _vm.setFleetItems(listFleet)
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.removeFleet("aa")
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueFleetState.FleetDeparted)
        collect.cancel()
    }
    @Test
    fun `removeFleetTest, when index smaller 0`() = runTest {
        val listFleet = ArrayList<FleetItem>()

        listFleet.add(
            FleetItem(
                1, "aa", "bb"
            )
        )
        _vm.setFleetItems(listFleet)
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.removeFleet("cc")
        runCurrent()

        Assertions.assertEquals(0, _events.size)
        collect.cancel()
    }
    @Test
    fun requestDepartTest() = runTest {
        val listFleet = FleetItem(
            1, "aa", "bb"
        )
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.requestDepart(listFleet)
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueFleetState.RequestDepartFleet)
        collect.cancel()
    }
    @Test
    fun addFleetTest() = runTest {
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.addFleet()
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueFleetState.AddFleet)
        collect.cancel()
    }
    @Test
    fun showSearchQueueTest() = runTest {
        val listFleet = FleetItem(
            1, "aa", "bb"
        )
        val collect = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.showSearchQueue(
            fleetItem = listFleet,
            currentQueueId = "cc",
            locationId = 1,
            subLocationId = 1
        )
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueFleetState.SearchQueueToDepart)
        collect.cancel()
    }
}