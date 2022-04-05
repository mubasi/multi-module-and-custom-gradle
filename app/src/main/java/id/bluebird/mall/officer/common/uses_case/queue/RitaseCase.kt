package id.bluebird.mall.officer.common.uses_case.queue

import id.bluebird.mall.officer.ui.home.model.QueueCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


interface RitaseCase {
    operator fun invoke(
        currentQueue: QueueCache?,
        waiting: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel>
}

class RitaseCaseImpl : RitaseCase {
    override fun invoke(
        currentQueue: QueueCache?,
        waiting: HashMap<Long, QueueCache>
    ): Flow<QueueCaseModel> = flow {
        if (waiting.isEmpty()) {
            emit(QueueCaseModel(QueueCache(number = -1)))
        }
        val list = waiting.values.toList().sortedByDescending { it.number }
        val newCurrentQueue = list.last()
        newCurrentQueue.isCurrentQueue = true
        waiting.remove(newCurrentQueue.number)
        emit(QueueCaseModel(newCurrentQueue, waitingQueue = waiting))
    }

}