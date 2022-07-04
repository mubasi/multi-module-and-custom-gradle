package id.bluebird.mall.home.utils

import id.bluebird.mall.home.model.QueueCache

sealed interface HomeDialogState {
    object Idle : HomeDialogState
    data class SkipCurrentQueue(val item: QueueCache) : HomeDialogState
    data class RestoreQueue(val item: QueueCache) : HomeDialogState
    data class SuccessCurrentQueue(val queueNumber: String) : HomeDialogState
}