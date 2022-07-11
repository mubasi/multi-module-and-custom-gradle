package id.bluebird.mall.feature_queue_fleet.main

import com.orhanobut.hawk.Hawk
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserById
import id.bluebird.mall.domain.user.model.CreateUserResult
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.domain_fleet.model.CountResult
import id.bluebird.mall.feature_queue_fleet.model.UserInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

    private lateinit var vm: QueueFleetViewModel
    private val getCount: GetCount = mockk()
    private val getUserById: GetUserById = mockk()

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        vm = QueueFleetViewModel(getCount, getUserById)
    }

    @AfterEach
    fun shutdown() {

    }

    @Test
    fun `initUserId, given userId is null, QueueFleetState is GetUserInfoSuccess`() = runTest {
        // Given
        val events = mutableListOf<QueueFleetState>()

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { getUserById.invoke(any()) } returns flow {
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
            vm.queueFleetState.collect {
                events.add(it)
            }
        }

        vm.initUserId(null)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(QueueFleetState.Idle, events.first())
        Assertions.assertEquals(
            QueueFleetState.GetUserInfoSuccess,
            events.last()
        )
    }

    @Test
    fun `initUserId, given userId is notnull, QueueFleetState is GetUserInfoSuccess`() = runTest {
        // Given
        val events = mutableListOf<QueueFleetState>()

        // Mock
        every { getUserById.invoke(any()) } returns flow {
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
            vm.queueFleetState.collect {
                events.add(it)
            }
        }
        vm.initUserId(1)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(QueueFleetState.Idle, events.first())
        Assertions.assertEquals(
            QueueFleetState.GetUserInfoSuccess,
            events.last()
        )
    }

    @Test
    fun `initUserId, given userId is notnull, QueueFleetState is FailedGetUser`() = runTest {
        // Given
        val events = mutableListOf<QueueFleetState>()

        // Mock
        every { getUserById.invoke(any()) } returns flow {
            throw NullPointerException(ERROR)
        }

        // Execute
        val job = launch {
            vm.queueFleetState.collect {
                events.add(it)
            }
        }
        vm.initUserId(1)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(QueueFleetState.Idle, events.first())
        Assertions.assertEquals(
            QueueFleetState.FailedGetUser(ERROR),
            events.last()
        )
    }

    @Test
    fun `initUserId, given userId is less than 0, result QueueFleetState is FailedGetUser`() =
        runTest {
            // Given
            val events = mutableListOf<QueueFleetState>()

            // Mock
            every { getUserById.invoke(any()) } returns flow {
                emit(GetUserByIdState.UserIdIsWrong)
            }

            // Execute
            val job = launch {
                vm.queueFleetState.collect {
                    events.add(it)
                }
            }
            vm.initUserId(-1)
            runCurrent()
            job.cancel()

            // Result
            Assertions.assertEquals(2, events.size)
            Assertions.assertEquals(QueueFleetState.Idle, events.first())
            Assertions.assertEquals(
                QueueFleetState.FailedGetUser(QueueFleetViewModel.ERROR_USER_ID),
                events.last()
            )
        }

    @Test
    fun `getCounter, given userInfo, Result success`() = runTest {
        // Given
        vm.setUserInfo(UserInfo(10))

        // Mock
        every { getCount.invoke(any()) } returns flow {
            emit(GetCountState.Success(CountResult(10, 11, 12)))
        }

        // Execute

        vm.getCounter()
        runCurrent()

        // Result
        Assertions.assertEquals(10, vm.counterLiveData.value!!.stock)
        Assertions.assertEquals(11, vm.counterLiveData.value!!.ritase)
        Assertions.assertEquals(12, vm.counterLiveData.value!!.request)
    }

    @Test
    fun `getCounter, given userInfo, Result throw error`() = runTest {
        // Given
        vm.setUserInfo(UserInfo(10))
        val events = mutableListOf<QueueFleetState>()

        // Mock
        every { getCount.invoke(any()) } returns flow {
            throw NullPointerException(ERROR)
        }

        // Execute
        val job = launch {
            vm.queueFleetState.collect {
                events.add(it)
            }
        }
        vm.getCounter()
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(QueueFleetState.Idle, events.first())
        Assertions.assertEquals(
            QueueFleetState.FailedGetCounter(ERROR),
            events.last()
        )
    }
}