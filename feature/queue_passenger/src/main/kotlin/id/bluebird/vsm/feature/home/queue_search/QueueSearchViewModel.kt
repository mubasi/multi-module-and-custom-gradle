package id.bluebird.vsm.feature.home.queue_search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.SearchQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SearchQueue
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.model.SearchQueueCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import proto.QueuePangkalanOuterClass

class QueueSearchViewModel(
    private val searchQueue: SearchQueue
) : ViewModel() {
    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }
    private val _queueSearchState : MutableSharedFlow<QueueSearchState> =
        MutableSharedFlow()
    val queueSearchState = _queueSearchState.asSharedFlow()
    val params: MutableLiveData<String> = MutableLiveData("")
    var listQueue : SearchQueueCache = SearchQueueCache("", queues = ArrayList<QueueReceiptCache>())

    fun filter() {
        viewModelScope.launch {
            if(params.value.toString().length > 2) {
                _queueSearchState.emit(QueueSearchState.ProsesSearchQueue)
            }

            if(params.value.toString().isEmpty()) {
                _queueSearchState.emit(QueueSearchState.ClearSearchQueue)
            }
        }
    }

    fun searchFilter(
        locationId: Long,
        subLocationId: Long,
        typeQueue : QueuePangkalanOuterClass.QueueType
    ) {
        viewModelScope.launch {
            searchQueue.invoke(
                queueNumber = params.value.toString(),
                locationId = locationId,
                subLocationId = subLocationId,
                typeQueue = typeQueue
            ).flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queueSearchState.emit(
                        QueueSearchState.FailedSearchQueue(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect{
                    when(it) {
                        is SearchQueueState.Success -> {
                            val tempListQueue = ArrayList<QueueReceiptCache>()
                            it.searchQueueResult.queues.forEach { result ->
                                tempListQueue.add(
                                    QueueReceiptCache(
                                        result.id,
                                        result.number
                                    )
                                )
                            }

                            listQueue = SearchQueueCache(
                                it.searchQueueResult.search_type,
                                tempListQueue
                            )

                            _queueSearchState.emit(
                                QueueSearchState.SuccessSearchQueue
                            )
                        }
                    }
                }
        }
    }


    fun prosesDeleteQueue(queueReceiptCache: QueueReceiptCache){
        viewModelScope.launch {
            _queueSearchState.emit(QueueSearchState.ProsesDeleteQueueSkipped(queueReceiptCache))
        }
    }

    fun prosesRestoreQueue(queueReceiptCache: QueueReceiptCache){
        viewModelScope.launch {
            _queueSearchState.emit(QueueSearchState.ProsesRestoreQueueSkipped(queueReceiptCache))
        }
    }

}