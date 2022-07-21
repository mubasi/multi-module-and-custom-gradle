package id.bluebird.mall.feature_queue_fleet.main

import com.orhanobut.hawk.Hawk
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserId
import id.bluebird.mall.domain.user.model.CreateUserResult
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.GetListFleetState
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.domain_fleet.domain.cases.GetListFleet
import id.bluebird.mall.domain_fleet.model.CountResult
import id.bluebird.mall.domain_fleet.model.FleetItemResult
import id.bluebird.mall.feature_queue_fleet.TestCoroutineRule
import id.bluebird.mall.feature_queue_fleet.model.CountCache
import id.bluebird.mall.feature_queue_fleet.model.FleetItem
import id.bluebird.mall.feature_queue_fleet.model.UserInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var _vm: QueueFleetViewModel
    private val _getCount: GetCount = mockk()
    private val _getUserId: GetUserId = mockk()
    private val _getFleetList: GetListFleet = mockk()
    private val _events = mutableListOf<QueueFleetState>()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = QueueFleetViewModel(_getCount, _getUserId, _getFleetList)
    }

    @AfterEach
    fun resetEvent() {
        _events.clear()
    }

    @Test
    fun `initUserId, given userId is null, QueueFleetState is GetUserInfoSuccess`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(null) } returns flow {
            emit(
                GetUserByIdState.Success(
                    CreateUserResult(
                        name = "aa",
                        username = "bb",
                        locationId = 10,
                        subLocationsId = mutableListOf(1, 2, 3, 4)
                    )
                )
            )
        }

        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.init()
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
    fun `initUserId, given userId is notnull, QueueFleetState is GetUserInfoSuccess`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getUserId.invoke(null) } returns flow {
            emit(
                GetUserByIdState.Success(
                    CreateUserResult(
                        name = "aa",
                        username = "bb",
                        locationId = 10,
                        subLocationsId = mutableListOf(1, 2, 3, 4)
                    )
                )
            )
        }

        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.init()
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
    fun `initUserId, given userId is notnull, QueueFleetState is FailedGetUser`() = runTest {
        // Mock
        every { _getUserId.invoke(null) } returns flow {
            throw NullPointerException(ERROR)
        }

        // Execute
        val job = launch {
            _vm.queueFleetState.toList(_events)
        }
        _vm.init()
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
        every { _getCount.invoke(any()) } returns flow {
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
        every { _getCount.invoke(any()) } returns flow {
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
            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(QueueFleetState.FailedGetList(exception), _events.last())
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
}