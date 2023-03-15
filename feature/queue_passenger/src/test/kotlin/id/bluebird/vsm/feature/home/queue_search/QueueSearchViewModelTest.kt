package id.bluebird.vsm.feature.home.queue_search

import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.model.QueueSearchCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
internal class QueueSearchViewModelTest {

    private lateinit var subjectUnderTest: QueueSearchViewModel
    private val states = mutableListOf<QueueSearchState>()
    private val error = "Error"

    @BeforeEach
    fun setUp() {
        subjectUnderTest = QueueSearchViewModel()
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `initTest set value list and prefix`() = runTest {
        //GIVEN
        val listQueue: ArrayList<QueueSearchCache> = ArrayList()
        val listWaiting: ArrayList<QueueReceiptCache> = ArrayList()
        val listSkipped: ArrayList<QueueReceiptCache> = ArrayList()
        for (i in 1..2) {
            val queueId = i.toLong()
            val queueName = "aa$i"
            listWaiting.add(
                QueueReceiptCache(
                    queueId = queueId,
                    queueNumber = queueName
                )
            )
            listQueue.add(
                QueueSearchCache(
                    queueId,
                    queueName,
                    true
                )
            )
        }
        for (y in 3..4) {
            val queueId = y.toLong()
            val queueName = "aa$y"
            listSkipped.add(
                QueueReceiptCache(
                    queueId = queueId,
                    queueNumber = queueName
                )
            )
            listQueue.add(
                QueueSearchCache(
                    queueId,
                    queueName,
                    false
                )
            )
        }
        val prefix = "cc"
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.init(listWaiting, listSkipped, prefix)
        runCurrent()
        delay(500)

        //THEN
        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(QueueSearchState.ProsesSearchQueue, states[0])
        Assertions.assertEquals(
            QueueSearchState.Idle, states[1]
        )
        Assertions.assertEquals(prefix, subjectUnderTest.getPrefix())
        collect.cancel()
    }

    @Test
    fun clearSearchTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.clearSearch()
        runCurrent()
        delay(500)
        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(QueueSearchState.Idle, states[0])
        collect.cancel()
    }

    @Test
    fun errorStateTest() = runTest {
        //given
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.errorState()
        runCurrent()
        delay(500)
        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(QueueSearchState.OnError, states[0])
        collect.cancel()
    }

    @Test
    fun `setFilterStatusTest when statusFilter is all`() = runTest {
        //given
        val status = QueueSearchViewModel.StatusFilter.ALL
        subjectUnderTest.setFilterStatus(status)

        Assertions.assertEquals(status, subjectUnderTest.getStatusFilter())
    }

    @Test
    fun `setFilterStatusTest when statusFilter is waiting`() = runTest {
        //given
        val status = QueueSearchViewModel.StatusFilter.WAITING
        subjectUnderTest.setFilterStatus(status)

        Assertions.assertEquals(status, subjectUnderTest.getStatusFilter())
    }

    @Test
    fun `setFilterStatusTest when statusFilter is skipped`() = runTest {
        //given
        val status = QueueSearchViewModel.StatusFilter.SKIPPED
        subjectUnderTest.setFilterStatus(status)

        Assertions.assertEquals(status, subjectUnderTest.getStatusFilter())
    }

    @Test
    fun `filterQueueTest when result is empty`() = runTest {
        val listQueue: ArrayList<QueueSearchCache> = ArrayList()
        for (i in 1..2) {
            val queueId = i.toLong()
            val queueName = "aa$i"
            listQueue.add(
                QueueSearchCache(
                    queueId,
                    queueName,
                    true
                )
            )
        }

        val prefix = "bb"
        val params = "cc"

        subjectUnderTest.setPrefix(prefix)
        subjectUnderTest.setParams(params)
        subjectUnderTest.setListQueue(listQueue)

        //given
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterQueue()
        runCurrent()
        delay(500)
        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(QueueSearchState.ErrorFilter, states[0])
        collect.cancel()
    }

    @Test
    fun `filterQueueTest when result is empty and params is null`() = runTest {
        val listQueue: ArrayList<QueueSearchCache> = ArrayList()
        for (i in 1..2) {
            val queueId = i.toLong()
            val queueName = "aa$i"
            listQueue.add(
                QueueSearchCache(
                    queueId,
                    queueName,
                    true
                )
            )
        }

        val prefix = "bb"
        val params = null

        subjectUnderTest.setPrefix(prefix)
        subjectUnderTest.setParams(params)
        subjectUnderTest.setListQueue(listQueue)

        //given
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterQueue()
        runCurrent()
        delay(500)
        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(QueueSearchState.ErrorFilter, states[0])
        collect.cancel()
    }

    @Test
    fun `filterQueueTest when result is not empty`() = runTest {
        val listQueue: ArrayList<QueueSearchCache> = ArrayList()
        for (i in 1..1) {
            val queueId = i.toLong()
            val queueName = "aa.$i"
            listQueue.add(
                QueueSearchCache(
                    queueId,
                    queueName,
                    true
                )
            )
        }

        val prefix = "aa"
        val params = "1"

        subjectUnderTest.setPrefix(prefix)
        subjectUnderTest.setParams(params)
        subjectUnderTest.setListQueue(listQueue)

        //given
        val collect = launch {
            subjectUnderTest.queueSearchState.toList(states)
        }

        //WHEN
        subjectUnderTest.filterQueue()
        runCurrent()
        delay(500)
        Assertions.assertEquals(1, states.size)
        Assertions.assertEquals(
            QueueSearchState.FilterResult(
                listQueue
            ), states[0]
        )
        collect.cancel()
    }
}