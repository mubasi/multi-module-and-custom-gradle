package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.domain_pasenger.model.CurrentQueue
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.QueueResult
import id.bluebird.mall.domain_pasenger.model.TakeQueueResult


sealed class GetQueueReceiptState {
    data class Success(val queueResult: QueueResult) : GetQueueReceiptState()
}

sealed class TakeQueueState {
    data class Success(val takeQueue: TakeQueueResult) : TakeQueueState()
}

sealed class WaitingQueueState<out T> {
    data class Success<out T>(val waitingQueue: List<Queue>) : WaitingQueueState<T>()
    object EmptyResult: WaitingQueueState<Nothing>()
}

sealed class GetCurrentQueueState {
    data class Success(val currentQueue: CurrentQueue): GetCurrentQueueState()
}