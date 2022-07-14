package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.domain_pasenger.model.QueueResult
import id.bluebird.mall.domain_pasenger.model.TakeQueueResult


sealed class GetQueueReceiptState {
    data class Success(val queueResult: QueueResult) : GetQueueReceiptState()
}

sealed class TakeQueueState {
    data class Success(val takeQueue: TakeQueueResult) : TakeQueueState()
}