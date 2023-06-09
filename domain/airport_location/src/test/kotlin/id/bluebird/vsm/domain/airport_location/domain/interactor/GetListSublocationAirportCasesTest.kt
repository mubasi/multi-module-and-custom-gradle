package id.bluebird.vsm.domain.airport_location.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_location.AirportLocationRepository
import id.bluebird.vsm.domain.airport_location.GetListSublocationAirportState
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
internal class GetListSublocationAirportCasesTest {

    private val repository: AirportLocationRepository = mockk()
    private lateinit var cases: GetListSublocationAirportCases

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        cases = GetListSublocationAirportCases(repository)
    }

    @Test
    fun `GetSubLocationByLocationIdCases, when list is empty`() = runTest {
        //given
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.getSubLocationByLocationIdAirport(
            any(), any(), any(),
        ) } returns flow {
            emit(
                LocationOuterClass.GetSubLocationByLocationResp.newBuilder()
                    .build()
            )
        }

        //when
        flowOf(cases.invoke(1L, showDeposition = false, showWingsChild = true)).test {
            Assertions.assertEquals(
                GetListSublocationAirportState.EmptyResult , awaitItem().singleOrNull()
            )
            awaitComplete()
        }
    }

    @Test
    fun `GetSubLocationByLocationIdCases, when list is not empty`() = runTest {
        //given
        every { Hawk.get<Long>(any()) } returns 1L
        every { repository.getSubLocationByLocationIdAirport(
            any(), any(), any(),
        ) } returns flow {
            emit(
                LocationOuterClass.GetSubLocationByLocationResp.newBuilder()
                    .apply {
                        this.addSubLocationList(
                            LocationOuterClass.SubLocationItems.newBuilder()
                                .apply {
                                    this.subLocationId = 1L
                                    this.subLocationName = "aa"
                                    this.subLocationType = "bb"
                                    this.isDeposistion = false
                                    this.isWings = false
                                }.build()
                        )
                    }
                    .build()
            )
        }

        //when
        flowOf(cases.invoke(1L, showDeposition = false, showWingsChild = true)).test {
            assert(awaitItem().single() is GetListSublocationAirportState.Success)
            awaitComplete()
        }
    }

}