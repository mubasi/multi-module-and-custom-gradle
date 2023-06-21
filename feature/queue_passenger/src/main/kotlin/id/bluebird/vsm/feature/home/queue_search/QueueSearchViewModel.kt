package id.bluebird.vsm.feature.home.queue_search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.model.QueueSearchCache
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class QueueSearchViewModel : ViewModel() {
    companion object {
        const val EMPTY_STRING = ""
    }

    private val _queueSearchState: MutableSharedFlow<QueueSearchState> =
        MutableSharedFlow()
    val queueSearchState = _queueSearchState.asSharedFlow()
    val params: MutableLiveData<String> = MutableLiveData(EMPTY_STRING)
    var listQueue: ArrayList<QueueSearchCache> = ArrayList()
    private var _prefix: String = EMPTY_STRING
    private var _statusFilter: StatusFilter = StatusFilter.ALL

    @VisibleForTesting
    fun getPrefix(): String {
        return _prefix
    }

    @VisibleForTesting
    fun getStatusFilter(): StatusFilter {
        return _statusFilter
    }

    @VisibleForTesting
    fun setPrefix(result: String) {
        _prefix = result
    }

    @VisibleForTesting
    fun setParams(result: String?) {
        params.value = result ?: EMPTY_STRING
    }

    @VisibleForTesting
    fun setListQueue(result: List<QueueSearchCache>) {
        listQueue.addAll(result)
    }

    fun init(
        listWaiting: ArrayList<QueueReceiptCache>,
        listSkipped: ArrayList<QueueReceiptCache>,
        prefix: String
    ) {
        viewModelScope.launch {
            _queueSearchState.emit(
                QueueSearchState.ProsesSearchQueue
            )
            setQueueList(listWaiting, true)
            setQueueList(listSkipped, false)
            _prefix = prefix
            _queueSearchState.emit(
                QueueSearchState.Idle
            )
        }
    }

    private fun setQueueList(dataList: ArrayList<QueueReceiptCache>, isWaiting: Boolean) {
        dataList.forEach {
            listQueue.add(
                QueueSearchCache(
                    it.queueId,
                    it.queueNumber,
                    isWaiting
                )
            )
        }
    }

    fun clearSearch() {
        viewModelScope.launch {
            params.value = EMPTY_STRING
            _queueSearchState.emit(QueueSearchState.Idle)
        }
    }

    fun errorState() {
        viewModelScope.launch {
            _queueSearchState.emit(QueueSearchState.OnError)
        }
    }

    fun setFilterStatus(status: StatusFilter) {
        _statusFilter = status
    }

    fun filterQueue() {
        viewModelScope.launch {
            if (resultFilterLocation().isEmpty()) {
                _queueSearchState.emit(QueueSearchState.ErrorFilter)
            } else {
                _queueSearchState.emit(QueueSearchState.FilterResult(resultFilterLocation()))
            }
        }
    }

    private fun resultFilterLocation(): ArrayList<QueueSearchCache> {
        val filteredlist: ArrayList<QueueSearchCache> = ArrayList()
        val dataParams = "$_prefix.${params.value ?: EMPTY_STRING}".toLowerCase()
        for (item in listQueue) {
            if (_statusFilter == StatusFilter.ALL) {
                if (item.queueNumber.toLowerCase().contains(dataParams)) {
                    filteredlist.add(item)
                }
            } else {
                if (item.queueNumber.toLowerCase()
                        .contains(dataParams) && item.isWaiting == (_statusFilter == StatusFilter.WAITING)
                ) {
                    filteredlist.add(item)
                }
            }
        }
        filteredlist.sortBy { it.queueNumber }
        return filteredlist
    }

    enum class StatusFilter {
        ALL, WAITING, SKIPPED
    }

}