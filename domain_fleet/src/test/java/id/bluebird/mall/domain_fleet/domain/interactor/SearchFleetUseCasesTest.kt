package id.bluebird.mall.domain_fleet.domain.interactor

import app.cash.turbine.test
import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.SearchFleetState
import id.bluebird.mall.domain_fleet.domain.cases.SearchFleet
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.FleetOuterClass

@ExperimentalCoroutinesApi
internal class SearchFleetUseCasesTest {

    private val _repository: FleetRepository = mockk()
    private lateinit var _searchFleet: SearchFleet

    @BeforeEach
    fun setup() {
        _searchFleet = SearchFleetUseCases(_repository)
    }

    private fun getFleetNumbers(size: Int): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until size) {
            list.add("$i")
        }
        return list
    }

    @Test
    fun `searchFleet, param isNull, result fleetNumbers size 10`() = runTest {
        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until 10) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke(null)).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(10))
            )
            awaitComplete()
        }
    }

    @Test
    fun `searchFleet, param isBlank, result fleetNumbers size 10`() = runTest {
        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until 10) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke(" ")).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(10))
            )
            awaitComplete()
        }
    }

    @Test
    fun `searchFleet, param isEmpty, result fleetNumbers size 10`() = runTest {
        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until 10) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke("")).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(10))
            )
            awaitComplete()
        }
    }

    @Test
    fun `searchFleet, param length is 2, result fleetNumbers size 40`() = runTest {
        // Given
        val size = 40

        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until size) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke("aa")).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(size))
            )
            awaitComplete()
        }
    }

    @Test
    fun `searchFleet, param length is 4, result fleetNumbers size 30`() = runTest {
        // Given
        val size = 30

        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until size) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke("aaaa")).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(size))
            )
            awaitComplete()
        }
    }

    @Test
    fun `searchFleet, param length gt 4, result fleetNumbers size 20`() = runTest {
        // Given
        val size = 20

        // Mock
        every { _repository.searchFleet(any(), any()) } returns flow {
            val result = FleetOuterClass.SearchResponse.newBuilder()
                .apply {
                    for (i in 0 until size) {
                        this.addFleets(FleetOuterClass.Fleet.newBuilder().apply {
                            fleetNumber = "$i"
                        }.build())
                    }
                }.build()
            emit(result)
        }

        // Execute
        flowOf(_searchFleet.invoke("bbbbb")).test {
            // Result
            Assertions.assertEquals(
                awaitItem().single(),
                SearchFleetState.Success(getFleetNumbers(size))
            )
            awaitComplete()
        }
    }
}