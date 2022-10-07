package id.bluebird.vsm.feature.home.queue_search

import id.bluebird.vsm.domain.passenger.SearchQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SearchQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.SearchQueueResult
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.model.SearchQueueCache
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import proto.QueuePangkalanOuterClass
import java.lang.NullPointerException

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class QueueSearchViewModelTest {

    private lateinit var subjectUnderTest: QueueSearchViewModel
    private val searchQueue: SearchQueue = mockk()
    private val states = mutableListOf<QueueSearchState>()
    private val error = "error"

    @BeforeEach
    fun setUp() {
        subjectUnderTest = QueueSearchViewModel(searchQueue)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `filter, when param is empty, emit ClearSearchQueue`() = runTest {
        //GIVEN
        subjectUnderTest.params.value = ""
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filter()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.ClearSearchQueue, states[0])
    }

    @Test
    fun `filter, when param length is 1, do nothing`() = runTest {
        //GIVEN
        subjectUnderTest.params.value = "a"
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filter()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(0, states.size)
    }

    @Test
    fun `filter, when param length is more than 2, emit ProsesSearchQueue`() = runTest {
        //GIVEN
        subjectUnderTest.params.value = "agu"
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filter()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.ProsesSearchQueue, states[0])
    }

    @Test
    fun `filterSearch, when response success, emit SuccessSearchQueue`() = runTest {
        //GIVEN
        val locationId = 1L
        val subLocationId = 11L
        val typeQueue = QueuePangkalanOuterClass.QueueType.SEARCH_WAITING_QUEUE
        val searchQueueCache = SearchQueueCache("searchType", ArrayList(List(2) {
            QueueReceiptCache((it + 1).toLong(), "number")
        }))
        every { searchQueue.invoke(any(), any(), any(), any()) } returns flow {
            emit(SearchQueueState.Success(SearchQueueResult("searchType", ArrayList(List(2) {
                Queue(
                    (it + 1).toLong(),
                    "number",
                    "12-12-2022",
                    "message",
                    "currentQueue",
                    2L,
                    "12-12-2022",
                    11L
                )
            }))))
        }
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchFilter(locationId, subLocationId, typeQueue)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.SuccessSearchQueue, states[0])
        assertEquals(searchQueueCache, subjectUnderTest.listQueue)
    }

    @Test
    fun `filterSearch, when response throw error, emit SuccessSearchQueue`() = runTest {
        //GIVEN
        val locationId = 1L
        val subLocationId = 11L
        val typeQueue = QueuePangkalanOuterClass.QueueType.SEARCH_WAITING_QUEUE

        every { searchQueue.invoke(any(), any(), any(), any()) } returns flow {
            throw NullPointerException(error)
        }
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.searchFilter(locationId, subLocationId, typeQueue)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.FailedSearchQueue(error), states[0])
    }

    @Test
    fun `prosesDeleteQueue, with given parameter, emit ProsesDeleteQueueSkipped`() = runTest {
        //GIVEN
        val queueReceiptCache = QueueReceiptCache(1L, "queueNumber")
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDeleteQueue(queueReceiptCache)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.ProsesDeleteQueueSkipped(queueReceiptCache), states[0])
    }

    @Test
    fun `prosesRestoreQueue, with given parameter, emit ProsesRestoreQueueSkipped`() = runTest {
        //GIVEN
        val queueReceiptCache = QueueReceiptCache(1L, "queueNumber")
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesRestoreQueue(queueReceiptCache)
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueSearchState.ProsesRestoreQueueSkipped(queueReceiptCache), states[0])
    }
}