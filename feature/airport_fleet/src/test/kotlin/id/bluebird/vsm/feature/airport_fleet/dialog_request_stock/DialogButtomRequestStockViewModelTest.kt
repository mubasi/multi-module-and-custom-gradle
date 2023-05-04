package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.RequestTaxiDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RequestTaxiDepart
import id.bluebird.vsm.domain.airport_assignment.model.RequestTaxiModel
import id.bluebird.vsm.fleet_non_apsh.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DialogButtomRequestStockViewModelTest {

    @Rule
    private val requestTaxi: RequestTaxiDepart = mockk(relaxed = true)

    private lateinit var subjectUnderTest: DialogButtomRequestStockViewModel
    private val states = mutableListOf<DialogRequestStockState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        subjectUnderTest = DialogButtomRequestStockViewModel(
            requestTaxi,
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun initSubLocationTest() = runTest {
        //given
        val subLocationId : Long = 1
        val requestToId : Long = 2

        //when
        subjectUnderTest.initSubLocationId(
            subLocationId, requestToId
        )

        //then
        assertEquals(subLocationId, subjectUnderTest.getSubLocationId())
        assertEquals(requestToId, subjectUnderTest.getRequestToId())
    }

    @Test
    fun cancelFleetDialogTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.cancelFleetDialog()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRequestStockState.CancleDialog, states[0])
        collect.cancel()
    }

    @Test
    fun focusableEnableTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.focusableEnable()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRequestStockState.FocusState(true), states[0])
        collect.cancel()
    }

    @Test
    fun `subtractRequestFleetTest when counter under minimum counter `() = runTest {
        //given
        subjectUnderTest.setRequestTaxiCounter("1")

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.subtractRequestFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRequestStockState.FocusState(false), states[0])
        collect.cancel()
    }

    @Test
    fun `subtractRequestFleetTest when counter more minimum counter `() = runTest {
        //given
        subjectUnderTest.setRequestTaxiCounter("5")

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.subtractRequestFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRequestStockState.FocusState(false), states[0])
        assertEquals(subjectUnderTest.requestTaxiCounter.value, subjectUnderTest.getValCounter().toString())
        collect.cancel()
    }

    @Test
    fun `addRequestFleetTest and add one counter `() = runTest {
        //given
        subjectUnderTest.setRequestTaxiCounter("5")

        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.subtractRequestFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(DialogRequestStockState.FocusState(false), states[0])
        assertEquals(subjectUnderTest.requestTaxiCounter.value, subjectUnderTest.getValCounter().toString())
        collect.cancel()
    }

    @Test
    fun `sendFleetRequestTest when condition is error `() = runTest {
        //given
        subjectUnderTest.setRequestTaxiCounter("5")
        val result = Throwable(message = error)

        every { requestTaxi.invoke(any(),any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.sendFleetRequest()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogRequestStockState.SendRequestTaxiOnProgress, states[0])
        assertEquals(DialogRequestStockState.Err(result), states[1])
        collect.cancel()
    }

    @Test
    fun `sendFleetRequestTest when condition is success `() = runTest {
        //given
        every { requestTaxi.invoke(any(),any(), any()) } returns flow {
            emit(
                RequestTaxiDepartState.Success(
                    RequestTaxiModel(
                        message = "aa",
                        requestFrom = 1,
                        createdAt = "bb",
                        requestCount = 5
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.action.toList(states)
        }

        //WHEN
        subjectUnderTest.sendFleetRequest()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(2, states.size)
        assertEquals(DialogRequestStockState.SendRequestTaxiOnProgress, states[0])
        assertEquals(DialogRequestStockState.RequestSuccess(
            count = 5
        ), states[1])
        collect.cancel()
    }
}