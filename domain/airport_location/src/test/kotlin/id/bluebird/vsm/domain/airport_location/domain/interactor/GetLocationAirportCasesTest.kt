package id.bluebird.vsm.domain.airport_location.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_location.AirportLocationRepository
import id.bluebird.vsm.domain.airport_location.GetLocationAirportState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.LocationOuterClass

@ExperimentalCoroutinesApi
internal class GetLocationAirportCasesTest {

    private val repository: AirportLocationRepository = mockk()
    private lateinit var cases: GetLocationAirportCases

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        cases = GetLocationAirportCases(repository)
    }

    @Test
    fun `GetLocationAirportCases, when list is empty`() = runTest {
        //given
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.getLocationAirport() } returns flow {
            emit(
                LocationOuterClass.GetLocationsResponse.newBuilder()
                    .build()
            )
        }

        //when
        flowOf(cases.invoke()).test {
            Assertions.assertEquals(
                GetLocationAirportState.EmptyResult , awaitItem().singleOrNull()
            )
            awaitComplete()
        }
    }

    @Test
    fun `GetSubLocationByLocationIdCases, when list is not empty`() = runTest {
        //given
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.getLocationAirport() } returns flow {
            emit(
                LocationOuterClass.GetLocationsResponse.newBuilder()
                    .apply {
                        addListLocations(
                            LocationOuterClass.CreateLocationRequest.newBuilder()
                                .apply {
                                    this.id = 1L
                                    this.locationName = "aa"
                                    this.isActive = 1
                                    this.createdAt = "bb"
                                    this.modifiedAt = "cc"
                                    this.createdBy = 2L
                                    this.modifiedBy = 3L
                                    this.codeArea = "dd"
                                    this.intervalReset = 4L
                                }.build()
                        )
                    }.build()
            )
        }

        //when
        flowOf(cases.invoke()).test {
            assert(awaitItem().single() is GetLocationAirportState.Success)
            awaitComplete()
        }
    }
}