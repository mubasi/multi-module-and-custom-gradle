package id.bluebird.vsm.feature.airport_fleet.add_by_camera

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddFleetAirport
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.ArrivedItemModel
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class AddByCameraAirportViewModelTest {

    private val addStock: AddFleetAirport = mockk(relaxed = true)

    private lateinit var subjectUnderTest: AddByCameraAirportViewModel
    private val states = mutableListOf<AddByCameraState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        subjectUnderTest = AddByCameraAirportViewModel(
            addStock
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun initTest() = runTest {
        //given
        val subLocationId = 1L

        //when
        subjectUnderTest.init(subLocationId)
        runCurrent()

        //then
        assertEquals(
            subLocationId, subjectUnderTest.subLocationId.value
        )
    }

    @Test
    fun cancelScanTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.addByCameraState.toList(states)
        }

        //when
        subjectUnderTest.cancleScan()
        runCurrent()

        //then
        assertEquals(1, states.size)
        assertEquals(
            AddByCameraState.CancleScan, states[0]
        )
        collect.cancel()
    }

    @Test
    fun repeatTakePictureTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.addByCameraState.toList(states)
        }

        //when
        subjectUnderTest.repeatTakePicture()
        runCurrent()

        //then
        assertEquals(1, states.size)
        assertEquals(
            AddByCameraState.RepeatTakePicture, states[0]
        )
        collect.cancel()
    }

    @Test
    fun `proseScanTest when condition is error`() = runTest {
        //given
        subjectUnderTest.param.value = "aa"
        subjectUnderTest.subLocationId.value = 1L
        val result = Throwable(message = error)
        every { UserUtils.getLocationId() } returns 1L
        every { addStock.invoke(any(), any(), any(), any()) } returns flow {
            throw result
        }

        val collect = launch {
            subjectUnderTest.addByCameraState.toList(states)
        }

        //when
        subjectUnderTest.proseScan()
        runCurrent()
        delay(500)

        //then
        assertEquals(1, states.size)
        assert(
            states[0] is
            AddByCameraState.OnError,
        )
        collect.cancel()
    }

    @Test
    fun `proseScanTest when condition is success`() = runTest {
        //given
        subjectUnderTest.param.value = "aa"
        subjectUnderTest.subLocationId.value = 1L

        val tempListTaxiNo = ArrayList<String>()
        val tempArrivedItem = ArrayList<ArrivedItemModel>()
        for (i in 1 .. 5) {
            tempArrivedItem.add(
                ArrivedItemModel(
                    stockId = 1L,
                    createdAt = "aa$i",
                    taxiNo = "bb$i"
                )
            )
            tempListTaxiNo.add("aa$i")
        }
        every { UserUtils.getLocationId() } returns 1L
        every {
            addStock.invoke(any(), any(), any(), any())
        } returns flow {
            emit(
                StockDepartState.Success(
                    AddStockDepartModel(
                        massage = "aa",
                        createdAt = "cc",
                        stockId = 1L,
                        stockType = "dd",
                        currentTuSpace = 1L,
                        arrivedItem = tempArrivedItem,
                        taxiList = tempListTaxiNo
                    )
                )
            )
        }

        val collect = launch {
            subjectUnderTest.addByCameraState.toList(states)
        }

        //when
        subjectUnderTest.proseScan()
        runCurrent()

        //then
        assertEquals(1, states.size)
        assert(
            states[0] is
                    AddByCameraState.ProsesScan,
        )
        collect.cancel()
    }

}