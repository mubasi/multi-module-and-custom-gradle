package id.bluebird.vsm.feature.airport_fleet.add_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddFleetAirport
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import id.bluebird.vsm.fleet_non_apsh.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import proto.AssignmentOuterClass

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class AddFleetViewModelNonApshTest {

    private val addFleetAirport: AddFleetAirport = mockk(relaxed = true)
    private val searchFleet: SearchFleet = mockk(relaxed = true)
    private lateinit var subjectUnderTest: AddFleetViewModelNonApsh
    private var states = mutableListOf<AddFleetState>()
    private val result = Throwable(message = "error")

    @BeforeEach
    fun setUp() {
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        subjectUnderTest = AddFleetViewModelNonApsh(
            addFleetAirport, searchFleet
        )
    }

    @Test
    fun `initTest, when param search is empty and result is success`() = runTest {
        //given
        every { searchFleet.invoke(any()) } returns flow {
            emit(
                SearchFleetState.Success(
                    listOf()
                )
            )
        }
        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.init(false, 2L)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(3, states.size)
        assertEquals(AddFleetState.ProgressSearch, states[0])
        assertEquals(AddFleetState.FleetsReset, states[1])
        assertEquals(AddFleetState.SearchFleetSuccess(listOf()), states[2])
        assertEquals(false, subjectUnderTest.getIsPerimeter())
        assertEquals(2L, subjectUnderTest.getSubLocationId())
        collect.cancel()

    }

    @Test
    fun `initTest, when param search is empty and result is error`() = runTest {
            //given
            every { searchFleet.invoke(any()) } returns flow {
                throw result
            }
            val collect = launch {
                subjectUnderTest.addFleetState.toList(states)
            }

            //WHEN
            subjectUnderTest.init(false, 2L)
            runCurrent()
            delay(500)

            //THEN
            assertEquals(3, states.size)
            assertEquals(AddFleetState.ProgressSearch, states[0])
            assertEquals(AddFleetState.FleetsReset, states[1])
            assertEquals(AddFleetState.SearchFleetError(result), states[2])
            assertEquals(false, subjectUnderTest.getIsPerimeter())
            assertEquals(2L, subjectUnderTest.getSubLocationId())
            collect.cancel()

        }

    @Test
    fun `searchFleetTest, when param search is null and result is success`() = runTest {
        //given
        every { searchFleet.invoke(any()) } returns flow {
            emit(
                SearchFleetState.Success(
                    listOf()
                )
            )
        }
        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchFleet()
        runCurrent()
        delay(500)

        //THEN
        assertEquals(3, states.size)
        assertEquals(AddFleetState.ProgressSearch, states[0])
        assertEquals(AddFleetState.FleetsReset, states[1])
        assertEquals(AddFleetState.SearchFleetSuccess(listOf()), states[2])
        collect.cancel()

    }

    @Test
    fun addFleet() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.addFleet("aa")
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assertEquals(AddFleetState.ShowDialogAddFleet("aa"), states[0])
        collect.cancel()

    }

    @Test
    fun `addFleetFromButtonTest when fleetNumber under 5 character`() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.addFleetFromButton("aa", false)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assert(
            states[0] is AddFleetState.AddFleetError
        )
        collect.cancel()
    }

    @Test
    fun `addFleetFromButtonTest when fleetNumber more 5 character and result is success`() = runTest {
        //given
        every { UserUtils.getLocationId() } returns 1L
        every {
            addFleetAirport.invoke(
                any(), any(), any(), any()
            )
        } returns flow {
            emit(
                StockDepartState.Success(
                    AddStockDepartModel(
                        "aa",
                        1L,
                        "bb",
                        "cc",
                        2L,
                        listOf(),
                        listOf()
                    )
                )
            )
        }

        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.addFleetFromButton("aabbcc", false)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assert(
            states[0] is AddFleetState.AddFleetSuccess
        )
        collect.cancel()
    }

    @Test
    fun `addFleetFromButtonTest when fleetNumber more 5 character and result is error`() = runTest {
        //given
        every { UserUtils.getLocationId() } returns 1L
        every {
            addFleetAirport.invoke(
                any(), any(), any(), any()
            )
        } returns flow {
            throw result
        }

        val collect = launch {
            subjectUnderTest.addFleetState.toList(states)
        }

        //WHEN
        subjectUnderTest.addFleetFromButton("aabbcc", false)
        runCurrent()
        delay(500)

        //THEN
        assertEquals(1, states.size)
        assert(
            states[0] is AddFleetState.AddFleetError
        )
        collect.cancel()
    }

}