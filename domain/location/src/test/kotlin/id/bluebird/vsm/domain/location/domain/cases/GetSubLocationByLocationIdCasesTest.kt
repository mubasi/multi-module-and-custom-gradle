package id.bluebird.vsm.domain.location.domain.cases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.domain.location.model.SubLocationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.OutletLocationPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class GetSubLocationByLocationIdCasesTest {

    private val repository: LocationRepository = mockk()
    private lateinit var cases: GetSubLocationByLocationId

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        cases = GetSubLocationByLocationIdCases(repository)
    }

    @Test
    fun `getSubLocationByLocationId, return list`() = runTest {
        //GIVEN
        val defaultLocationId = 1L
        val defaultSubLocationId = 2L
        val defaultSubLocationName = "Test"
        val prefix = "prefix"
        val havePengendapan = false
        val idPengendapan = 1L
        every { Hawk.get<Long>(any()) } returns defaultLocationId
        every { repository.getSubLocationByLocationId(defaultLocationId) } returns flow {
            emit(
                OutletLocationPangkalanOuterClass.GetSubLocationPangkalanByLocationResp.newBuilder()
                    .addSubLocationList(OutletLocationPangkalanOuterClass.SubLocationPangkalanItems.newBuilder().apply {
                        this.subLocationId = defaultSubLocationId
                        this.subLocationName = defaultSubLocationName
                        this.prefix = prefix
                        this.havePengedapan = havePengendapan
                        this.idPengendapan = idPengendapan
                    }.build()).build()
            )
        }

        //WHEN
        flowOf(cases.invoke(defaultLocationId)).test {

            //THEN
            assertEquals(
                LocationDomainState.Success(
                    listOf(
                        SubLocationResult(
                            defaultSubLocationId,
                            defaultSubLocationName,
                            prefix,
                            havePengendapan,
                            idPengendapan
                        )
                    )
                ), awaitItem().singleOrNull()
            )
            awaitComplete()
        }
    }

    @Test
    fun `getSubLocationByLocationId, return empty list`() = runTest {
        //GIVEN
        val defaultLocationId = 1L
        every { Hawk.get<Long>(any()) } returns defaultLocationId
        every { repository.getSubLocationByLocationId(defaultLocationId) } returns flow {
            emit(
                OutletLocationPangkalanOuterClass.GetSubLocationPangkalanByLocationResp.newBuilder().build()
            )
        }

        //WHEN
        flowOf(cases.invoke(defaultLocationId)).test {

            //THEN
            assertEquals(
                LocationDomainState.Success(listOf<SubLocationResult>()), awaitItem().singleOrNull()
            )
            awaitComplete()
        }
    }
}