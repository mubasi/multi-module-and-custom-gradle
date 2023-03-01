package id.bluebird.vsm.feature.home.qr_code

import id.bluebird.vsm.domain.location.GetLocationQrCodeState
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationQrCode
import id.bluebird.vsm.domain.location.model.GetLocationQrCodeResult
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class QrCodeViewModelTest {

    companion object {
        const val ERROR = "error"
    }


    private lateinit var subjectTest: QrCodeViewModel
    private val getSubLocationQrCode: GetSubLocationQrCode = mockk(relaxed = true)
    private val _events = mutableListOf<QrCodeState>()

    @BeforeEach
    fun setup() {
        subjectTest = QrCodeViewModel(
            getSubLocationQrCode
        )
    }

    @AfterEach
    fun tearDown() {
        _events.clear()
    }

    @Test
    fun `initTest`() = runTest {
        val locationId: Long = 1
        val subLocationId: Long = 2
        val titleLocation = "aa"

        subjectTest.init(locationId, subLocationId, titleLocation)

        Assertions.assertEquals(locationId, subjectTest._locationId)
        Assertions.assertEquals(subLocationId, subjectTest._subLocationId)
        Assertions.assertEquals(titleLocation, subjectTest.titleLocation.value)
    }

    @Test
    fun `changeQrCode when qrCodeDriver and qrCodeWeb is null and error Test`() = runTest {
        val result = Throwable(message = ERROR)

        every { getSubLocationQrCode.invoke(any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectTest.qrCodeState.toList(_events)
        }

        //WHEN
        subjectTest.changeQrCode(1)
        runCurrent()
        delay(500)

        Assertions.assertEquals(2, _events.size)
        Assertions.assertEquals(
            QrCodeState.Progress, _events[0]
        )
        Assertions.assertEquals(
            QrCodeState.OnError(result), _events[1]
        )
        collect.cancel()
    }

    @Test
    fun `changeQrCode when qrCodeDriver and qrCodeWeb is null and success in position driver Test`() =
        runTest {
            every { getSubLocationQrCode.invoke(any()) } returns flow {
                emit(
                    GetLocationQrCodeState.Success(
                        GetLocationQrCodeResult(
                            subLocationId = 1,
                            locationId = 2,
                            subLocationName = "aa",
                            daQrCode = "bb",
                            queuePassengerQrCode = "cc"
                        )
                    )
                )
            }
            val collect = launch {
                subjectTest.qrCodeState.toList(_events)
            }

            //WHEN
            subjectTest.changeQrCode(0)
            runCurrent()
            delay(500)

            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(
                QrCodeState.Progress, _events[0]
            )
            Assertions.assertEquals(
                QrCodeState.SuccessLoad("bb"), _events[1]
            )
            Assertions.assertEquals("bb", subjectTest.qrCodeDriver)
            Assertions.assertEquals("cc", subjectTest.qrCodeWeb)
            collect.cancel()
        }

    @Test
    fun `changeQrCode when qrCodeDriver and qrCodeWeb is null and success in position queuePassenger Test`() =
        runTest {
            every { getSubLocationQrCode.invoke(any()) } returns flow {
                emit(
                    GetLocationQrCodeState.Success(
                        GetLocationQrCodeResult(
                            subLocationId = 1,
                            locationId = 2,
                            subLocationName = "aa",
                            daQrCode = "bb",
                            queuePassengerQrCode = "cc"
                        )
                    )
                )
            }
            val collect = launch {
                subjectTest.qrCodeState.toList(_events)
            }

            //WHEN
            subjectTest.changeQrCode(1)
            runCurrent()
            delay(500)

            Assertions.assertEquals(2, _events.size)
            Assertions.assertEquals(
                QrCodeState.Progress, _events[0]
            )
            Assertions.assertEquals(
                QrCodeState.SuccessLoad("cc"), _events[1]
            )
            Assertions.assertEquals("bb", subjectTest.qrCodeDriver)
            Assertions.assertEquals("cc", subjectTest.qrCodeWeb)
            collect.cancel()
        }

    @Test
    fun `changeQrCode when qrCodeDriver and qrCodeWeb is not null and success in position driver Test`() =
        runTest {

            subjectTest.setQrCode(0, "aa")
            subjectTest.setQrCode(1, "bb")

            val collect = launch {
                subjectTest.qrCodeState.toList(_events)
            }

            //WHEN
            subjectTest.setQrCode(0)
            runCurrent()
            delay(500)

            Assertions.assertEquals(1, _events.size)
            Assertions.assertEquals(
                QrCodeState.SuccessLoad("aa"), _events[0]
            )
            Assertions.assertEquals("aa", subjectTest.qrCodeDriver)
            Assertions.assertEquals("bb", subjectTest.qrCodeWeb)
            collect.cancel()
        }


    @Test
    fun `changeQrCode when qrCodeDriver and qrCodeWeb is not null and success in position queuePassenger Test`() =
        runTest {

            subjectTest.setQrCode(0, "aa")
            subjectTest.setQrCode(1, "bb")

            val collect = launch {
                subjectTest.qrCodeState.toList(_events)
            }

            //WHEN
            subjectTest.setQrCode(1)
            runCurrent()
            delay(500)

            Assertions.assertEquals(1, _events.size)
            Assertions.assertEquals(
                QrCodeState.SuccessLoad("bb"), _events[0]
            )
            Assertions.assertEquals("aa", subjectTest.qrCodeDriver)
            Assertions.assertEquals("bb", subjectTest.qrCodeWeb)
            collect.cancel()
        }


}