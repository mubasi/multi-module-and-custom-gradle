package id.bluebird.mall.officer.common.uses_case.queue

import id.bluebird.mall.officer.ui.home.model.QueueCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RestoreQueueCases {
    operator fun invoke(
        currentQueue: QueueCache?,
        restoreQueue: QueueCache,
        waitingQueue: HashMap<Long, QueueCache>,
        delayQueue: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel>
}

class RestoreQueueCasesImpl : RestoreQueueCases {
    override fun invoke(
        currentQueue: QueueCache?,
        restoreQueue: QueueCache,
        waitingQueue: HashMap<Long, QueueCache>,
        delayQueue: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel> = flow {
        try {
            currentQueue?.let {
                removeCurrentQueue(currentQueue, waitingQueue, delayQueue)
                emit(
                    QueueCaseModel(
                        restoreQueue(restoreQueue, delayQueue),
                        delayQueue,
                        waitingQueue
                    )
                )
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
        delayQueue: HashMap<Long, QueueCache>,
    ) {
        currentQueue.isCurrentQueue = false
        if (currentQueue.isDelay) {
            delayQueue[currentQueue.number] = currentQueue
        } else {
            waitingQueue[currentQueue.number] = currentQueue
        }
    }

    private fun restoreQueue(
        newQueueCache: QueueCache,
        delayQueue: HashMap<Long, QueueCache>
    ): QueueCache {
        newQueueCache.isCurrentQueue = true
        delayQueue.remove(newQueueCache.number)
        return newQueueCache
    }
}