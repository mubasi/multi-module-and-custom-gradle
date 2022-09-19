package id.bluebird.vsm.domain.fleet.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.model.FleetDepartResult
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
internal class DepartFleetUseCasesTest {

    private val repository: FleetRepository = mockk()
    private lateinit var departFleetUseCases: DepartFleetUseCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        departFleetUseCases = DepartFleetUseCases(repository)
    }

    private fun getFleetNumbers(size: Int): List<Long> {
        val list: MutableList<Long> = mutableListOf()
        for (i in 0 until size) {
            list.add(i.toLong())
        }
        return list
    }

    @Test
    fun `departFleetUseCases, isSuccess` () = runTest {
        //given

        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.departFleet(
                any(), any(), any(), any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentPangkalanOuterClass.StockResponse.newBuilder()
                    .apply {
                        message = "vv"
                        stockType = "AA"
                        stockId = 1
                        createdAt = "2022-07-18T07:54:14Z"
                    }.build()
            )
        }

        flowOf(departFleetUseCases.invoke(
            1,
            "aa",
            false,
            getFleetNumbers(2),
            "bb"
        )).test {
            //result

            Assertions.assertEquals(
                awaitItem().single(),
                DepartFleetState.Success(
                    FleetDepartResult(
                        taxiNo = "AA",
                        message = "vv",
                        stockType = "AA",
                        stockId = "1",
                        createdAt = "2022-07-18T07:54:14Z"
                    )
                )
            )
            awaitComplete()
        }

    }
}