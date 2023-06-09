package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.model.CountSubLocationItem
import id.bluebird.vsm.domain.airport_assignment.model.GetSubLocationAirportModel
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
internal class GetSubLocationAirportCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var getSubLocationAirportCases: GetSubLocationAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getSubLocationAirportCases = GetSubLocationAirportCases(repository)
    }

    @Test
    fun `GetSubLocationAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getSubLocationAssignmentByLocationId(
                any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.ResponseSubLocation.newBuilder()
                    .apply {
                        this.locationName = "aa"
                        this.locationId = 1L
                        addSubLocationItems(
                            AssignmentOuterClass.CountSubLocationItems.newBuilder()
                                .apply {
                                    this.subLocationName ="cc"
                                    this.count = 2L
                                    this.subLocationId = 3L
                                    this.withPassenger = false
                                }.build()
                        )
                    }.build()
            )
        }

        flowOf(
            getSubLocationAirportCases.invoke(
                1L, false, 0
            )
        ).test {
            //result
            val tempCountSubLocationItem = ArrayList<CountSubLocationItem>()
            tempCountSubLocationItem.add(
                CountSubLocationItem(
                    subLocationName = "cc",
                    count = 2L,
                    subLocationId = 3L,
                    withPassenger = false
                )
            )


            Assertions.assertEquals(
                awaitItem().single(), GetSubLocationAirportState.Success(
                    GetSubLocationAirportModel(
                        locationName = "aa",
                        locationId = 1L,
                        countSubLocationItem = tempCountSubLocationItem
                    )
                )
            )
            awaitComplete()
        }
    }

}