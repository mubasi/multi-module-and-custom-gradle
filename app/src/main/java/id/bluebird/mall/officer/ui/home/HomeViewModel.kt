package id.bluebird.mall.officer.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.ui.home.dialog.Action
import id.bluebird.mall.officer.utils.DateUtils
import kotlinx.coroutines.*
import kotlin.random.Random

class HomeViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    ViewModel() {

    companion object {
        const val MAX_TIMER = 30
    }

    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<CommonState> = MutableLiveData()
    var homeState = _homeState

    val taxiNumber: MutableLiveData<String> = MutableLiveData()
    val currentQueue: MutableLiveData<QueueCache> = MutableLiveData()
    val delayCallTimer: MutableLiveData<Int> = MutableLiveData(30)
    val searchQueue: MutableLiveData<String> = MutableLiveData()
    val locationName: MutableLiveData<String> = MutableLiveData()
    val lastSync: MutableLiveData<String> = MutableLiveData("Last sync : 11 Jan 2022 • 13:48")
    val subLocationName: MutableLiveData<String> = MutableLiveData("Lobby Utara")
    val counter: MutableLiveData<CounterModel> = MutableLiveData(CounterModel())

    init {
        locationName.value = "Gandaria City, ${DateUtils.getTodayDate()}"
        currentQueue.value = QueueCache(999, isDelay = false, isCurrentQueue = true)
    }

    fun logout() {
        _homeState.value = HomeState.Logout
    }

    fun sync() {
        viewModelScope.launch(dispatcher) {
            _homeState.postValue(HomeState.OnSync)
            delay(2000)
            randomCounter()
            lastSync.postValue("Last sync: ${DateUtils.getLastSycnFormat()}")
            _homeState.postValue(CommonState.Idle)
        }
    }

    /** random() used for counter dummy value*/
    private fun randomCounter() {
        val random1 = Random.nextLong(0, 1000)
        val random2 = Random.nextLong(0, 100)
        val random3 = Random.nextLong(0, 100)
        counter.postValue(CounterModel(random1, random2, random3))
    }

    private fun delay() {
        viewModelScope.launch(dispatcher) {
            while (isActive) {
                delay(1000)
                delayCallTimer.value?.let {
                    if (it > 30) {
                        cancel()
                    }
                    delayCallTimer.postValue(it.plus(1))
                }
            }
        }
    }

    fun callCurrentQueue() {
        delayCallTimer.value?.let {
            if ((it in 1..29).not()) {
                delayCallTimer.value = 0
                delay()
            }
        }
    }

    fun skipCurrentQueue(item: QueueCache) {
        _homeState.value = HomeState.SkipCurrentQueue(item)
    }

    fun successCurrentQueue(queue: String) {
        _homeState.value = HomeState.SuccessCurrentQueue(queue)
    }

    fun actionSearch() {
        when {
            searchQueue.value.isNullOrEmpty() -> {
                _homeState.value = HomeState.ParamSearchQueueEmpty
            }
            searchQueue.value != null -> {
                searchQueue.value?.let {
                    if (it.length < 2) {
                        _homeState.value = HomeState.ParamSearchQueueLessThanTwo
                    } else {
                        // implementation search
                        _homeState.value = CommonState.Idle
                    }
                }
            }
        }
    }

    fun submitBottomSheet(action: Action, item: QueueCache) {
        if (action == Action.SKIP) {
            _homeState.value = HomeState.SuccessSkiped(item.getQueue())
        }
    }

    fun submitRitaseDialog() {
        taxiNumber.value = ""
        _homeState.value = HomeState.SuccessRitase(currentQueue.value?.getQueue() ?: "-")
    }

    fun dummyIndicator() {
        _homeState.value = HomeState.DummyIndicator
    }

    fun changeIndicator(isConnected: Boolean) {
        connectionState.value = isConnected
    }
}