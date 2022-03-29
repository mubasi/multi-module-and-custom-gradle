package id.bluebird.mall.officer.case.queue

import id.bluebird.mall.officer.ui.home.model.QueueCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SkipQueueCases {
    operator fun invoke(
        currentQueue: QueueCache?,
        waitingQueue: HashMap<Long, QueueCache>,
        delayQueue: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel>
}

class SkipQueueCasesImpl : SkipQueueCases {
    override fun invoke(
        currentQueue: QueueCache?,
        waitingQueue: HashMap<Long, QueueCache>,
        delayQueue: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel> = flow {
        try {
            currentQueue?.let {
                removeCurrentQueue(currentQueue, waitingQueue, delayQueue)
                emit(QueueCaseModel(getNextQueue(waitingQueue), delayQueue, waitingQueue))
            } ?: run {
                throw NullPointerException()
            }
        } catch (e: Exception) {
            error(e)
        }
    }

    private fun removeCurrentQueue(
        currentQueue: QueueCache,
        waitingQueue: HashMap<Long, QueueCache>,
        delayQueue: HashMap<Long, QueueCache>
    ) {
        waitingQueue.remove(currentQueue.number)
        currentQueue.isDelay = true
        currentQueue.isCurrentQueue = false
        delayQueue[currentQueue.number] = currentQueue
    }

    private fun getNextQueue(waitingQueue: HashMap<Long, QueueCache>): QueueCache? {
        if (waitingQueue.isEmpty()) {
            return null
        }
        val newCurrentQueue = if (waitingQueue.size == 1) {
            waitingQueue.values.first()
        } else {
            val sorted = waitingQueue.values.sortedByDescending { it.number }
            sorted.last()
        }
        newCurrentQueue.isCurrentQueue = true
        waitingQueue.remove(newCurrentQueue.number)
        return newCurrentQueue
    }
}

