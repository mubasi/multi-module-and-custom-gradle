package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.domain_pasenger.model.CurrentQueueResult
import id.bluebird.mall.domain_pasenger.model.QueueResult
import id.bluebird.mall.domain_pasenger.model.SkipQueueResult
import id.bluebird.mall.domain_pasenger.model.TakeQueueResult


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