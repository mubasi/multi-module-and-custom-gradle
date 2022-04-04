package id.bluebird.mall.officer.common.uses_case.queue

import androidx.annotation.Keep
import id.bluebird.mall.officer.ui.home.model.QueueCache

@Keep
data class QueueCaseModel(
    val currentQueue: QueueCache?,
    val delayQueue: HashMap<Long, QueueCache> = HashMap(),
    val waitingQueue: HashMap<Long, QueueCache> = HashMap()
)