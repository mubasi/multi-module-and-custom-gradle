package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.RequestTaxiDepartState
import id.bluebird.vsm.domain.airport_assignment.model.RequestTaxiModel
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
internal class RequestTaxiDepartCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var requestTaxiDepartCases: RequestTaxiDepartCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        requestTaxiDepartCases = RequestTaxiDepartCases(repository)
    }

    @Test
    fun `RequestTaxiDepartCases, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.requestTaxiDepart(
                any(),any(),any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.RequestTaxiResponse.newBuilder()
                    .apply {
                        this.message = "aa"
                        this.requestFrom = 1L
                        this.createdAt = "bb"
                        this.requestCount = 2L
                    }.build()
            )
        }

        flowOf(
            requestTaxiDepartCases.invoke(
                1L, 2L, 3L
            )
        ).test {
            //result
            Assertions.assertEquals(
                awaitItem().single(),
                RequestTaxiDepartState.Success(
                    RequestTaxiModel(
                        message = "aa",
                        requestFrom = 1L,
                        createdAt = "bb",
                        requestCount = 2L
                    )
                )
            )
            awaitComplete()
        }
    }
}