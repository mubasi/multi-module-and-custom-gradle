package id.bluebird.vsm.domain.location.domain.cases

import app.cash.turbine.test
import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.domain.location.model.LocationsWithSub
import id.bluebird.vsm.domain.location.model.SubLocationResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.LocationPangkalanOuterClass
import java.lang.NullPointerException

@ExperimentalCoroutinesApi
internal class GetLocationsWithSubUseCasesTest {
    private val repository: LocationRepository = mockk(relaxed = true)
    private lateinit var cases: GetLocationsWithSub

    @BeforeEach
    fun setup() {
        cases = GetLocationsWithSubUseCases(repository)
    }

    @Test
    fun `getLocationsWithSubUseCase, return success with 1 location`() = runTest {
        //GIVEN
        val locationId = 1L
        val locationName = "testLocation"
        val subLocationSize = 2
        val subLocationName = "testSubLocation"
        every { repository.getLocations() } returns flow {
            emit(
                LocationPangkalanOuterClass.GetLocationsResponse.newBuilder().apply {
                    this.addListLocations(LocationPangkalanOuterClass.CreateLocationRequest.newBuilder().apply {
                        this.id = locationId
                        this.locationName = locationName
                    })
                }.build()
            )
        }
        every { repository.getSubLocations() } returns flow {
            emit(
                LocationPangkalanOuterClass.GetSubLocationsResponse.newBuilder().apply {
                    for (i in 0 until subLocationSize) {
                        this.addSubLocations(
                            LocationPangkalanOuterClass.GetSubLocationsItem.newBuilder().apply {
                                this.locationId = locationId
                                this.subLocationId = i.toLong()
                                this.subLocationName = subLocationName
                            }.build()
                        )
                    }
                }.build()
            )
        }

        //WHEN
        flowOf(cases.invoke()).test {
            //THEN
            assertEquals(
                GetLocationsWithSubState.Success(
                    HashMap(mutableMapOf(Pair(locationId, LocationsWithSub(
                        locationId, locationName, MutableList(subLocationSize) {
                            SubLocationResult(it.toLong(), subLocationName)
                        }
                    ))))
                ), awaitItem().singleOrNull())
            awaitComplete()
        }
    }
}