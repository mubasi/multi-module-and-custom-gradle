package id.bluebird.vsm.feature.home.queue_search

import id.bluebird.vsm.feature.home.model.QueueSearchCache

sealed class QueueSearchState {
    object ProsesSearchQueue : QueueSearchState()
    data class SuccessSearchQueue(
        val result: ArrayList<QueueSearchCache>
    ) : QueueSearchState()

    object Idle : QueueSearchState()
    object OnError : QueueSearchState()
    object ClearSearchQueue : QueueSearchState()
    object ErrorFilter : QueueSearchState()
    data class FilterResult(val result: ArrayList<QueueSearchCache>) : QueueSearchState()
}