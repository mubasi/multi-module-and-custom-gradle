package id.bluebird.vsm.feature.home.queue_ticket

import id.bluebird.vsm.domain.passenger.domain.cases.GetQueueReceipt
import id.bluebird.vsm.feature.home.TestCoroutineRule
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.queue_search.QueueSearchState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class QueueTicketViewModelTest {

    private lateinit var subjectUnderTest: QueueTicketViewModel
    private val getQueueReceipt: GetQueueReceipt = mockk()
    private val states = mutableListOf<QueueTicketState>()

    @BeforeEach
    fun setUp() {
        subjectUnderTest = QueueTicketViewModel(getQueueReceipt)
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `prosesDialog, emit ProsesTicket`() = runTest {
        //GIVEN
        val collect = launch {
            subjectUnderTest.queueTicketState.toList(states)
        }

        //WHEN
        subjectUnderTest.prosesDialog()
        runCurrent()
        collect.cancel()

        //THEN
        assertEquals(1, states.size)
        assertEquals(QueueTicketState.ProsesTicket, states[0])
    }
}