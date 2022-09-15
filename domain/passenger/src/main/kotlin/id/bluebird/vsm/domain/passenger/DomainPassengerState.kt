package id.bluebird.vsm.domain.passenger

import id.bluebird.vsm.domain.passenger.model.*


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
sealed class WaitingQueueState {
    data class Success(val waitingQueue: List<Queue>) : WaitingQueueState()
    object EmptyResult: WaitingQueueState()
}

sealed class DeleteSkippedState {
    data class Success(val queueResult: QueueResult) : DeleteSkippedState()
}

sealed class RestoreSkippedState {
    data class Success(val queueResult: QueueResult) : RestoreSkippedState()
}

sealed class CounterBarState {
    data class Success(val counterBarResult: CounterBarResult) : CounterBarState()
}

sealed class SearchQueueState {
    data class Success(val searchQueueResult: SearchQueueResult) : SearchQueueState()
}