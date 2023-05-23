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
internal class AddFleetAirportCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var addFleetAirportCases: AddFleetAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        addFleetAirportCases = AddFleetAirportCases(repository)
    }

    @Test
    fun `AddFleetAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.addFleetAirport(
                any(), any(), any(), any()
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
            addFleetAirportCases.invoke(
                1L,
                "aa",
                1L,
                 false,
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
                        currentTuSpace = 0
                    )
                )
            )
            awaitComplete()
        }

    }
}