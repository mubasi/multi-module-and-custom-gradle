package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationStockCountDepartState
import id.bluebird.vsm.domain.airport_assignment.model.StockCountModel
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
import proto.AssignmentOuterClass

@ExperimentalCoroutinesApi
internal class GetSubLocationStockCountDepartCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var getSubLocationStockCountDepartCases: GetSubLocationStockCountDepartCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getSubLocationStockCountDepartCases = GetSubLocationStockCountDepartCases(repository)
    }

    @Test
    fun `GetSubLocationStockCountDepartCases, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getSubLocationStockCountDepart(
                any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.StockCountResponse.newBuilder()
                    .apply {
                        this.stock = 1L
                        this.request = 2L
                        this.ritase = 3L
                    }.build()
            )
        }

        flowOf(
            getSubLocationStockCountDepartCases.invoke(
                subLocationId = 1L,
                locationId = 2L,
                todayEpoch = 3L
            )
        ).test {
            //result
            Assertions.assertEquals(
                awaitItem().single(),
                GetSubLocationStockCountDepartState.Success(
                    StockCountModel(
                        1L, 2L, 3L
                    )
                )
            )
            awaitComplete()
        }

    }

}