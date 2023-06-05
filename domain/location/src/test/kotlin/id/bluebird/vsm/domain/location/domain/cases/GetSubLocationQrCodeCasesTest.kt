package id.bluebird.vsm.domain.location.domain.cases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.location.GetLocationQrCodeState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationQrCode
import id.bluebird.vsm.domain.location.model.GetLocationQrCodeResult
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
import proto.OutletLocationPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class GetSubLocationQrCodeCasesTest {

    companion object {
        const val ERROR = "error"
    }

    private val repository: LocationRepository = mockk()
    private lateinit var cases: GetSubLocationQrCode

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        cases = GetSubLocationQrCodeCases(repository)
    }

    @Test
    fun `getSubLocationQrCode when condition is success`() = runTest {
        //given
        val defaultLocationId = 1L
        val subLocationId = 2L
        every { Hawk.get<Long>(any()) } returns defaultLocationId
        every { repository.getSubLocationQrCode(subLocationId) } returns flow {
            emit(
                OutletLocationPangkalanOuterClass.ResponseGetLocationPangkalanQrCode.newBuilder()
                    .apply {
                        this.subLocationId = subLocationId
                        this.locationId = defaultLocationId
                        this.subLocationName = "aa"
                        this.daQrCode = "bb"
                        this.queuePassangerQrCode = "cc"
                    }.build()
            )
        }

        //execute
        flowOf(cases.invoke(subLocationId)).test {
            //result

            Assertions.assertEquals(
                awaitItem().single(),
                GetLocationQrCodeState.Success(
                    GetLocationQrCodeResult(
                        subLocationId = subLocationId,
                        locationId = defaultLocationId,
                        subLocationName = "aa",
                        daQrCode = "bb",
                        queuePassengerQrCode = "cc"
                    )
                )
            )
            awaitComplete()
        }
    }
}