package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.domain_pasenger.model.*


sealed class GetQueueReceiptState {
    data class Success(val queueResult: QueueResult) : GetQueueReceiptState()
}

sealed class TakeQueueState {
    data class Success(val takeQueue: TakeQueueResult) : TakeQueueState()
}

sealed class GetCurrentQueueState {
    data class Success(val currentQueueResult: CurrentQueueResult) : GetCurrentQueueState()
}

sealed class SkipQueueState {
    data class Success(val skipQueueResult: SkipQueueResult) : SkipQueueState()
}

sealed class ListQueueWaitingState {
    data class Success(val listQueueResult: ListQueueResult) : ListQueueWaitingState()
}

sealed class ListQueueSkippedState {
    data class Success(val listQueueResult: ListQueueResult) : ListQueueSkippedState()
}
sealed class WaitingQueueState<out T> {
    data class Success<out T>(val waitingQueue: List<Queue>) : WaitingQueueState<T>()
    object EmptyResult: WaitingQueueState<Nothing>()
}

sealed class DeleteSkippedState {
    data class Success(val queueResult: QueueResult) : DeleteSkippedState()
}