package id.bluebird.vsm.feature.queue_car_fleet.main

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.extensions.StringExtensions
import id.bluebird.vsm.core.extensions.StringExtensions.getLastSync
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
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserByIdForAssignment
import id.bluebird.vsm.domain.user.model.UserAssignment
import id.bluebird.vsm.feature.queue_car_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_car_fleet.model.CountCacheCarFleet
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import id.bluebird.vsm.feature.queue_car_fleet.model.UserRoleInfo
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
internal class QueueCarFleetViewModelTest {

    companion object {

        private const val ERROR = "error"
    }

    private lateinit var _vm: QueueCarFleetViewModel
    private val _getCount: GetCount = mockk(relaxed = true)
    private val _getUserId: GetUserByIdForAssignment = mockk(relaxed = true)
    private val _getFleetList: GetListFleet = mockk(relaxed = true)
    private val _departFleet: DepartFleet = mockk(relaxed = true)
    private val _events = mutableListOf<QueueCarFleetState>()

    @BeforeEach
    fun setup() {
        LocationNavigationTemporary.setTestingVariable(true)
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        mockkObject(LocationNavigationTemporary)
        _vm = QueueCarFleetViewModel(
            _getCount,
            _getUserId,
            _getFleetList,
            _departFleet,
        )
    }

    @AfterEach
    fun tearDown() {
        LocationNavigationTemporary.setTestingVariable(false)
        _events.clear()
    }

    @Test
    fun `getUserById, when isSuccess and create title by response then createTitle and Success`() = runTest {
        val userAssignment =
            UserAssignment(
                id = 1L,
                locationId = 8,
                locationName = "Location",
                subLocationId = 2L,
                subLocationName = "Sub Location ${StringExtensions.SUFFIX_TEST}",
                prefix = "prefix"
            )
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(any(), any(), any()) } returns flow {
            emit(GetUserByIdForAssignmentState.Success(userAssignment))
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.runTestGetUserById()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events.first())
        Assertions.assertEquals(
            QueueCarFleetState.GetUserInfoSuccess,
            _events.last()
        )
        Assertions.assertEquals("${userAssignment.locationName} ${userAssignment.subLocationName}".getLastSync(), _vm.titleLocation!!.value)
    }

    @Test
    fun `getUserById, when isSuccess and create location temp then createTitle and Success`() = runTest {
        val userAssignment =
            UserAssignment(
                id = 1L,
                locationId = 8,
                locationName = "Location",
                subLocationId = 2L,
                subLocationName = "Sub Location ${StringExtensions.SUFFIX_TEST}",
                isOfficer = true,
                prefix = "prefix"
            )
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(any(), any(), any()) } returns flow {
            emit(GetUserByIdForAssignmentState.Success(userAssignment))
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.runTestGetUserById()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events.first())
        Assertions.assertEquals(
            QueueCarFleetState.GetUserInfoSuccess,
            _events.last()
        )
        Assertions.assertEquals(
            "${LocationNavigationTemporary.locationName} ${LocationNavigationTemporary.subLocationName}".getLastSync(),
            _vm.titleLocation.value
        )
    }

    @Test
    fun `getUserById, when isSuccess and create location temp then createTitle and Success and is Not Officer`() =
        runTest {
            val userAssignment =
                UserAssignment(
                    id = 1L,
                    locationId = 8,
                    locationName = "Location",
                    subLocationId = 2L,
                    subLocationName = "Sub Location ${StringExtensions.SUFFIX_TEST}",
                    isOfficer = false,
                    prefix = "prefix"
                )
            // Mock
            every { Hawk.get<Long>(any()) } returns 1L
            every { LocationNavigationTemporary.getLocationNav() } returns LocationNavigation(
                locationId = 8,
                subLocationId = 2L,
                locationName = "Location",
                subLocationName = "Sub Location ${StringExtensions.SUFFIX_TEST}"
            )
            every { _getUserId.invoke(any(), any(), any()) } returns flow {
                emit(GetUserByIdForAssignmentState.Success(userAssignment))
            }
            // Execute
            val job = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            _vm.runTestGetUserById()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events.first())
            Assertions.assertEquals(
                QueueCarFleetState.GetUserInfoSuccess,
                _events.last()
            )
            Assertions.assertEquals(
                "${LocationNavigationTemporary.locationName} ${LocationNavigationTemporary.subLocationName}".getLastSync(),
                _vm.titleLocation.value
            )
        }


    @Test
    fun `getUserById, when isSuccess and create location temp then createTitle and Success and is Not Officer and locationNav is Null`() =
        runTest {
            val userAssignment =
                UserAssignment(
                    id = 1L,
                    locationId = 8,
                    locationName = "Location",
                    subLocationId = 2L,
                    subLocationName = "Sub Location ${StringExtensions.SUFFIX_TEST}",
                    isOfficer = false,
                    prefix = "prefix"
                )
            // Mock
            every { Hawk.get<Long>(any()) } returns 1L
            every { LocationNavigationTemporary.getLocationNav() } returns null
            every { _getUserId.invoke(any(), any(), any()) } returns flow {
                emit(GetUserByIdForAssignmentState.Success(userAssignment))
            }
            // Execute
            val job = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            _vm.runTestGetUserById()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events.first())
            Assertions.assertEquals(
                QueueCarFleetState.GetUserInfoSuccess,
                _events.last()
            )
            Assertions.assertEquals(" ".getLastSync(), _vm.titleLocation.value)
        }

    @Test
    fun `getUserById, isFailed`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(any(), any(), any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.runTestGetUserById()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events.first())
        Assertions.assertEquals(
            QueueCarFleetState.FailedGetUser(ERROR),
            _events.last()
        )
    }

    @Test
    fun `getCounter, given userInfo, Result success`() = runTest {
        // Given
        _vm.setUserInfo(UserRoleInfo(10))
        // Mock
        every { _getCount.invoke(any(), any()) } returns flow {
            emit(GetCountState.Success(CountResult(10, 11, 12, 13)))
        }
        // Execute
        _vm.getCounter()
        runCurrent()
        // Result
        Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
        Assertions.assertEquals(11, _vm.counterLiveData.value!!.ritase)
        Assertions.assertEquals(12, _vm.counterLiveData.value!!.request)
        Assertions.assertEquals(13, _vm.counterLiveData.value!!.depositionStock)
    }

    @Test
    fun `getCounter, given userInfo, Result throw error`() = runTest {
        // Given
        _vm.setUserInfo(UserRoleInfo(10))
        // Mock
        every { _getCount.invoke(any(), any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.getCounter()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(
            QueueCarFleetState.FailedGetCounter(ERROR),
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
        _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 11))
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.showRequestFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(QueueCarFleetState.ShowRequestCarFleet(11), _events.last())
    }

    @Test
    fun `idleState, result state is Idle`() = runTest {
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.stateIdle()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(QueueCarFleetState.Idle, _events.last())
    }

    @Test
    fun `searchFleet, result state is SearchFleet with subLocationId & FleetItems`() = runTest {
        // Pre
        _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 11))
        _vm.setFleetItems(mutableListOf())
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.searchFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(11, (_events.last() as QueueCarFleetState.SearchCarFleet).subLocationId)
        Assertions.assertEquals(
            emptyList<CarFleetItem>(),
            (_events.last() as QueueCarFleetState.SearchCarFleet).list
        )
        assert(_events.last() is QueueCarFleetState.SearchCarFleet)
    }

    @Test
    fun `getListFleet, given subLocationId, condition don't hit api, result QueueFleetState Success`() =
        runTest {
            // Result
            _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 12))
            val carFleetItems: MutableList<CarFleetItem> = mutableListOf()
            carFleetItems.add(CarFleetItem())
            _vm.setFleetItems(carFleetItems)
            // Execute
            val job = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            _vm.getFleetList()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(2, _events.size)
            assert(_events.last() is QueueCarFleetState.GetListSuccess)
            Assertions.assertEquals(
                1,
                (_events.last() as QueueCarFleetState.GetListSuccess).list.size
            )
        }

    @Test
    fun `getListFleet, given subLocationId, result QueueFleetState Success`() = runTest {
        // Result
        _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 12))
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
                        arriveAt = "2022-07-19T0${i}:03:13Z",
                        i
                    )
                )
            }
            emit(GetListFleetState.Success(list))
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.getFleetList()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueCarFleetState.GetListSuccess)
        Assertions.assertEquals(
            9,
            (_events.last() as QueueCarFleetState.GetListSuccess).list.size
        )
    }

    @Test
    fun `getListFleet, given subLocationId, result QueueFleetState Empty`() = runTest {
        // Result
        _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 12))
        // Mock
        every {
            _getFleetList.invoke(any())
        } returns flow {
            emit(GetListFleetState.EmptyResult)
        }
        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.getFleetList()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueCarFleetState.GetListEmpty, _events.last())
    }

    @Test
    fun `getListFleet, given subLocationId, throw exception, result QueueFleetState FailedGetList`() =
        runTest {
            // Result
            _vm.setUserInfo(UserRoleInfo(userId = 10, subLocationId = 12))
            val exception = NullPointerException()
            // Mock
            every {
                _getFleetList.invoke(any())
            } returns flow {
                throw exception
            }
            // Execute
            val job = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            _vm.getFleetList()
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(3, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetCarFleetList, _events.first())
            Assertions.assertEquals(QueueCarFleetState.FailedGetList(exception), _events[1])
            Assertions.assertEquals(QueueCarFleetState.GetListEmpty, _events.last())
        }

    @Test
    fun `addSuccess, pre CountCache stock is 10, condition fleetNumber is notBlank, result stock change to 11`() =
        runTest {
            // Pre
            _vm.setCountCache(CountCacheCarFleet(stock = 10))
            Assertions.assertEquals(10, _vm.counterLiveData.value!!.stock)
            // Execute
            val job = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            _vm.addSuccess(CarFleetItem(id = 1, name = "", arriveAt = ""))
            runCurrent()
            job.cancel()
            // Result
            Assertions.assertEquals(11, _vm.counterLiveData.value!!.stock)
            assert(_events.last() is QueueCarFleetState.GetListSuccess)
        }

    @Test
    fun `addSuccess, pre CountCache stock is 10, condition fleetNumber isBlank, result stock isNotChange`() =
        runTest {
            // Pre
            _vm.setCountCache(CountCacheCarFleet(stock = 10))
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
                _vm.queueCarFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            delay(500)
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressHolder, _events[0])
            Assertions.assertEquals(QueueCarFleetState.ToSelectLocation, _events[1])
            collect.cancel()
        }

    @Test
    fun `init, when user is not officer and locationNav available, emit getUserSuccess`() =
        runTest {
            //GIVEN
            val userAssignment =
                UserAssignment(
                    id = 1L,
                    locationId = 8,
                    locationName = "Location",
                    subLocationId = 2L,
                    subLocationName = "Sub Location",
                    prefix = "prefix"
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
            every { _getUserId.invoke(any(), any(),any()) } returns flow {
                emit(GetUserByIdForAssignmentState.Success(userAssignment))
            }
            val collect = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueCarFleetState.GetUserInfoSuccess, _events[1])
            Assertions.assertEquals(
                UserRoleInfo(1L, 8L, 2L),
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
            every { _getUserId.invoke(any(),any(),any()) } returns flow {
                throw NullPointerException(ERROR)
            }
            val collect = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueCarFleetState.FailedGetUser(ERROR), _events[1])
            Assertions.assertEquals(
                UserRoleInfo(),
                _vm.valUserInfo()
            )

            collect.cancel()
        }

    @Test
    fun `init, when user is officer and locationNav not available and failed to getUserState, emit failedGetUser`() =
        runTest {
            //GIVEN
            every { LocationNavigationTemporary.isLocationNavAvailable() } returns false
            every { UserUtils.isUserOfficer() } returns true
            every { UserUtils.getUserId() } returns 1L
            every { _getUserId.invoke(any(),any(),any()) } returns flow {
                emit(GetUserByIdForAssignmentState.UserNotFound)
            }
            val collect = launch {
                _vm.queueCarFleetState.toList(_events)
            }
            //WHEN
            _vm.init()
            runCurrent()
            //THEN
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueCarFleetState.ProgressGetUser, _events[0])
            Assertions.assertEquals(QueueCarFleetState.FailedGetUser(QueueCarFleetViewModel.ERROR_MESSAGE_UNKNOWN), _events[1])
            Assertions.assertEquals(
                UserRoleInfo(),
                _vm.valUserInfo()
            )

            collect.cancel()
        }


    @Test
    fun initLocationTest() = runTest {
        _vm.initLocation(1, 2)

        Assertions.assertEquals(1, _vm.mUserRoleInfo.locationId)
        Assertions.assertEquals(2, _vm.mUserRoleInfo.subLocationId)
    }

    @Test
    fun `initLocationTest, when location and sublocation smaller 0`() = runTest {
        _vm.initLocation(-1, -1)

        Assertions.assertEquals(-1, _vm.mUserRoleInfo.locationId)
        Assertions.assertEquals(-1, _vm.mUserRoleInfo.subLocationId)
    }

    @Test
    fun `departFleet, when with passenger and queueisBlank is Success`() = runTest {
        val withPassenger = false
        val queueId = ""
        val carFleetItem = CarFleetItem(
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
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.departFleet(carFleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueCarFleetState.SuccessDepartCarFleet)
        collect.cancel()
    }

    @Test
    fun `departFleet, when with passenger and queueisNotBlank is Error`() = runTest {
        val withPassenger = false
        val queueId = "aa"
        val carFleetItem = CarFleetItem(
            id = 1,
            name = "aa",
            arriveAt = "bb"
        )

        every { _departFleet.invoke(any(), any(), any(), any(), any(), any()) } returns flow {
            throw NullPointerException(ERROR)
        }
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.departFleet(carFleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueCarFleetState.FailedDepart)
        collect.cancel()
    }

    @Test
    fun `departFleet, when with passenger and queueisNotBlank is Success`() = runTest {
        val withPassenger = false
        val queueId = "aa"
        val carFleetItem = CarFleetItem(
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
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.departFleet(carFleetItem, withPassenger, queueId)
        runCurrent()

        Assertions.assertEquals(2, _events.size)
        assert(_events.last() is QueueCarFleetState.SuccessDepartCarFleet)
        collect.cancel()
    }

    @Test
    fun `removeFleetTest when list is not empty`() = runTest {
        val listFleet = arrayListOf(
            CarFleetItem(
                1, "aa", "bb"
            ),
            CarFleetItem(
                2, "cc", "dd"
            ),
        )
        _vm.setFleetItems(listFleet)
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.removeFleet("aa")
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueCarFleetState.GetListSuccess)
        collect.cancel()
    }

    @Test
    fun `removeFleetTest when list is empty`() = runTest {
        val listFleet = arrayListOf(
            CarFleetItem(
                1, "aa", "bb"
            ),
        )
        _vm.setFleetItems(listFleet)
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.removeFleet("aa")
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueCarFleetState.GetListEmpty)
        collect.cancel()
    }

    @Test
    fun `removeFleetTest, when index smaller 0`() = runTest {
        val listFleet = ArrayList<CarFleetItem>()

        listFleet.add(
            CarFleetItem(
                1, "aa", "bb"
            )
        )
        _vm.setFleetItems(listFleet)
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.removeFleet("cc")
        runCurrent()

        Assertions.assertEquals(0, _events.size)
        collect.cancel()
    }

    @Test
    fun requestDepartTest() = runTest {
        val listFleet = CarFleetItem(
            1, "aa", "bb"
        )
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.requestDepart(listFleet)
        advanceTimeBy(100)
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueCarFleetState.RequestDepartCarFleet)
        collect.cancel()
    }

    @Test
    fun addFleetTest() = runTest {
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.addFleet()
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueCarFleetState.AddCarFleet)
        collect.cancel()
    }

    @Test
    fun showSearchQueueTest() = runTest {
        val listFleet = CarFleetItem(
            1, "aa", "bb"
        )
        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.showSearchQueue(
            carFleetItem = listFleet,
            currentQueueId = "cc",
            locationId = 1,
            subLocationId = 1
        )
        runCurrent()

        Assertions.assertEquals(1, _events.size)
        assert(_events.last() is QueueCarFleetState.SearchQueueToDepartCar)
        collect.cancel()
    }


    @Test
    fun `qrCodeScreenTest, emit state QrCodeScreen`() = runTest {
        //GIVEN
        val locationName = "aa"
        val subLocationName = "bb"

        _vm.mUserRoleInfo = UserRoleInfo(1L, 1L, 11L)
        _vm.setLocationName(locationName)
        _vm.setSubLocationName(subLocationName)

        val result = "$locationName $subLocationName"

        val collect = launch {
            _vm.queueCarFleetState.toList(_events)
        }

        //WHEN
        _vm.goToQrCodeScreen()
        runCurrent()
        collect.cancel()

        //THEN
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(
            QueueCarFleetState.GoToQrCodeScreen(
                1L, 11L, result
            ), _events[0]
        )
    }

    @Test
    fun refreshTest() = runTest {

        val counter = CountCacheCarFleet()

        //mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { LocationNavigationTemporary.isLocationNavAvailable().not() } returns false
        every { UserUtils.isUserOfficer().not() } returns false

        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.refresh()
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(QueueCarFleetState.ProgressHolder, _events[0])
        Assertions.assertEquals(QueueCarFleetState.ToSelectLocation, _events[1])
        Assertions.assertEquals(0, _vm.getFleetItem().size)
        Assertions.assertEquals(counter, _vm.getCountCache())
        job.cancel()
    }

    @Test
    fun showRecordRitaseTest() = runTest {
        //given
        val carFleetItem = CarFleetItem(
            id = 1,
            name = "aa",
            arriveAt = "bb"
        )
        val queueId = "cc"
        _vm.mUserRoleInfo = UserRoleInfo(1L, 1L, 11L)


        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.showRecordRitase(carFleetItem, queueId)
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(
            QueueCarFleetState.RecordRitaseToDepart(
                carFleetItem,
                1L,
                11L,
                queueId
            ), _events[0]
        )
        job.cancel()
    }

    @Test
    fun onErrorFromDialogTest() = runTest {

        val result = Throwable(message = ERROR)

        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.onErrorFromDialog(result)
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(
            QueueCarFleetState.FailedGetQueueCar(
                result
            ), _events[0]
        )
        Assertions.assertEquals(
                QueueCarFleetState.GetListEmpty, _events[1]
        )
        job.cancel()
    }

    @Test
    fun `goToDepositionScreenTest, when title value is null and counter is null`() = runTest {
        //given
        _vm.setTitleLocation(null)
        _vm.setCountCache(null)
        _vm.setIdDeposition(1L)
        _vm.setUserInfo(
            UserRoleInfo(
                subLocationId = 1L
            )
        )

        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.goToDepositionScreen()
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(
            QueueCarFleetState.GotoDepositionScreen(
                QueueCarFleetViewModel.EMPTY_STRING,
                1L,
                1L,
                0
            ), _events[0]
        )
        job.cancel()
    }


    @Test
    fun `goToDepositionScreenTest, when title value is not null and counter is not null`() = runTest {
        //given
        _vm.setTitleLocation("aa")
        _vm.setCountCache(
            CountCacheCarFleet(
                stock = 1, request =  2, ritase = 3, depositionStock= 4
            )
        )
        _vm.setUserInfo(
            UserRoleInfo(
                subLocationId = 1L
            )
        )
        _vm.setIdDeposition(1L)

        // Execute
        val job = launch {
            _vm.queueCarFleetState.toList(_events)
        }
        _vm.goToDepositionScreen()
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(
            QueueCarFleetState.GotoDepositionScreen(
                "aa",
                1L,
                1L,
                4
            ), _events[0]
        )
        job.cancel()
    }
}