package id.bluebird.mall.officer.case.queue

import androidx.annotation.Keep
import id.bluebird.mall.officer.ui.home.QueueCache

@Keep
data class QueueCaseModel(
    val currentQueue: QueueCache?,
    val delayQueue: HashMap<Long, QueueCache>,
    val waitingQueue: HashMap<Long, QueueCache>
)