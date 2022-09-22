package id.bluebird.vsm.domain.location.domain.cases

import app.cash.turbine.test
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetLocations
import id.bluebird.vsm.domain.location.model.LocationResult
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

@ExperimentalCoroutinesApi
internal class GetLocationsCasesTest {
    private val repository: LocationRepository = mockk()
    private lateinit var getLocationCases: GetLocations

    @BeforeEach
    fun setup() {
        getLocationCases = GetLocationsCases(repository)
    }

    @Test
    fun `getLocations, return empty`() = runTest {
        //GIVEN
        every { repository.getLocations() } returns flow {
            emit(
                LocationPangkalanOuterClass.GetLocationsResponse.newBuilder().build()
            )
        }

        //WHEN
        flowOf(getLocationCases.invoke()).test {

            //THEN
            assertEquals(LocationDomainState.Empty, awaitItem().single())
            awaitComplete()
        }
    }

    @Test
    fun `getLocations, return list with size 5`() = runTest {
        //GIVEN
        val size = 5
        val locationName = "test"
        val isActive = false
        val codeArea = "000"
        every { repository.getLocations() } returns flow {
            emit(
                LocationPangkalanOuterClass.GetLocationsResponse.newBuilder().apply {
                    for (i in 0 until size) {
                        this.addListLocations(LocationPangkalanOuterClass.CreateLocationRequest.newBuilder().apply {
                            this.id = i.toLong()
                            this.locationName = locationName
                            this.isActive = if (isActive) 1 else 0
                            this.codeArea = codeArea
                        }.build())
                    }
                }.build()
            )
        }

        //WHEN
        flowOf(getLocationCases.invoke()).test {

            //THEN
            assertEquals(LocationDomainState.Success(List(size) {
                LocationResult(
                    id = it.toLong(),
                    locationName = locationName,
                    isActive = isActive,
                    codeArea = codeArea
                )
            }), awaitItem().singleOrNull())
            awaitComplete()
        }
    }
}