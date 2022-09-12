package id.bluebird.vsm.feature.home.utils

import id.bluebird.vsm.feature.home.model.QueueCache

sealed interface HomeDialogState {
    object Idle : HomeDialogState
    data class SkipCurrentQueue(val item: QueueCache) : HomeDialogState
    data class RestoreQueue(val item: QueueCache) : HomeDialogState
    data class SuccessCurrentQueue(val queueNumber: String) : HomeDialogState
}