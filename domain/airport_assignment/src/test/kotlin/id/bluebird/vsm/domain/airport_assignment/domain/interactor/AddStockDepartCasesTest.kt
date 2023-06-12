package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.ArrivedItemModel
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
internal class AddStockDepartCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var addStockDepartCases: AddStockDepartCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        addStockDepartCases = AddStockDepartCases(repository)
    }

    @Test
    fun `AddStockDepartCases, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.stockDepart(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.StockResponse.newBuilder()
                    .apply {
                        this.message = "aa"
                        this.stockType = "bb"
                        this.stockId = 1L
                        this.createdAt = "cc"
                        addArrivedFleet(
                            AssignmentOuterClass.ArrivedItem.newBuilder()
                                .apply {
                                    this.stockId = 1L
                                    this.taxiNo = "dd"
                                    this.createdAt = "ee"
                                }.build()
                        )
                        addTaxiNo(
                            "aa"
                        )
                    }.build()
            )
        }

        flowOf(
            addStockDepartCases.invoke(
                1L,
                2L,
                "aa",
                isWithPassenger = 1L,
                isArrived = false,
                queueNumber = "bb",
                departFleetItem = listOf()
            )
        ).test {
            //result
            val tempArrivedItem = ArrayList<ArrivedItemModel>()
            tempArrivedItem.add(
                ArrivedItemModel(
                    stockId = 1L,
                    createdAt = "ee",
                    taxiNo = "dd"
                )
            )
            Assertions.assertEquals(
                awaitItem().single(),
                StockDepartState.Success(
                    AddStockDepartModel(
                        massage = "aa",
                        stockId = 1L,
                        stockType = "bb",
                        createdAt = "cc",
                        taxiList = listOf("aa"),
                        arrivedItem = tempArrivedItem,
                        currentTuSpace = 0L
                    )
                )
            )
            awaitComplete()
        }
    }
}