package id.bluebird.mall.domain_fleet.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.model.CountResult
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
internal class GetCountCasesTest {

    private val repository: FleetRepository = mockk()
    private lateinit var getCountCases: GetCountCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getCountCases = GetCountCases(repository)
    }

    @Test
    fun `getCount, result is successs`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.getCount(any(), any(), any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.StockCountResponse.newBuilder()
                    .apply {
                        stock = 10
                        request = 10
                        ritase = 10
                    }.build()
            )
        }

        // Execute
        flowOf(getCountCases.invoke(1)).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                GetCountState.Success(CountResult(10, 10, 10))
            )
            awaitComplete()
        }
    }
}