package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetDetailRequestInLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.GetDetailRequestInLocationAirportModel
import id.bluebird.vsm.domain.airport_assignment.model.SubLocationItemAirportModel
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
internal class GetDetailRequestByLocationAirportCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var getDetailRequestByLocationAirportCases: GetDetailRequestByLocationAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getDetailRequestByLocationAirportCases = GetDetailRequestByLocationAirportCases(repository)
    }


    @Test
    fun `GetDetailRequestByLocationAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getDetailRequestInLocation(
                any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.ResponseGetDetailRequest.newBuilder()
                    .addSubLocationItems(
                        AssignmentOuterClass.DetailRequestItems.newBuilder()
                            .apply {
                                this.subLocationId = 1L
                                this.subLocationName = "aa"
                                this.count = 1
                            }.build()
                    ).build()
            )
        }


        flowOf(
            getDetailRequestByLocationAirportCases.invoke(
                1L,
                false,
            )
        ).test {
            //result
            val result : ArrayList<SubLocationItemAirportModel> = ArrayList()
            result.add(
                SubLocationItemAirportModel(
                    1L, "aa", 1
                )
            )
            Assertions.assertEquals(
                awaitItem().single(),
                GetDetailRequestInLocationAirportState.Success(
                    GetDetailRequestInLocationAirportModel(
                        result
                    )
                )
            )
            awaitComplete()
        }

    }

}