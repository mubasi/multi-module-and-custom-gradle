package id.bluebird.vsm.domain.fleet.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.fleet.AddFleetState
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.AssignmentPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class AddFleetUseCasesTest {

    private val repository: FleetRepository = mockk()
    private lateinit var addFleetUseCases : AddFleetUseCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        addFleetUseCases = AddFleetUseCases(repository)
    }

    @Test
    fun `AddFleetUseCases, isSuccess` () = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.addFleet(any(), any(), any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.StockResponse.newBuilder()
                    .apply {
                        message = ""
                        stockType = "AA"
                        stockId = 1
                        createdAt = "2022-07-18T07:54:14Z"
                    }.build()
            )
        }

        flowOf(addFleetUseCases.invoke("aa", 1)).test {
            //result
            Assertions.assertEquals(
                awaitItem().single(),
                AddFleetState.Success(
                    FleetItemResult(
                        fleetId = 1,
                        fleetName = "AA",
                        arriveAt = "2022-07-18T07:54:14Z"
                    )
                )
            )
            awaitComplete()
        }
    }

}
