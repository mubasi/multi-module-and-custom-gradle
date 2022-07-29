package id.bluebird.mall.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.CommonState
import id.bluebird.mall.core.HomeState
import id.bluebird.mall.core.extensions.StringExtensions.getLastSync
import id.bluebird.mall.core.extensions.StringExtensions.getTodayDate
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.domain.intercator.ForceLogout
import id.bluebird.mall.home.dialog.Action
import id.bluebird.mall.home.model.CounterModel
import id.bluebird.mall.home.model.QueueCache
import id.bluebird.mall.home.utils.HomeDialogState

import kotlinx.coroutines.*
import kotlin.random.Random

class HomeViewModel(
    private val logoutCases: ForceLogout,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    ViewModel() {

    companion object {
        const val MAX_TIMER = 30
    }

    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<HomeState> = MutableLiveData()
    var homeState = _homeState

    private val _homeDialogState: MutableLiveData<HomeDialogState> = MutableLiveData()
    var homeDialogState = _homeDialogState

    val subLocationInitial: MutableLiveData<String> = MutableLiveData("A.")
    val queueDelay: MutableLiveData<List<QueueCache>> = MutableLiveData()
    val queueWaiting: MutableLiveData<List<QueueCache>> = MutableLiveData()
    val taxiNumber: MutableLiveData<String> = MutableLiveData()
    val currentQueue: MutableLiveData<QueueCache> = MutableLiveData()
    val delayCallTimer: MutableLiveData<Int> = MutableLiveData(30)
    val searchQueue: MutableLiveData<String> = MutableLiveData()
    val locationName: MutableLiveData<String> = MutableLiveData()
    val lastSync: MutableLiveData<String> = MutableLiveData("Last sync : 11 Jan 2022 • 13:48")
    val subLocationName: MutableLiveData<String> = MutableLiveData("Lobby Utara")
    val counter: MutableLiveData<CounterModel> = MutableLiveData(CounterModel())
    private val delays: HashMap<Long, QueueCache> = HashMap()
    private val waitings: HashMap<Long, QueueCache> = HashMap()
    private var mTempCurrentQueue: QueueCache = QueueCache()

    init {
        locationName.value = "Gandaria City, ${"".getTodayDate()}}"
        currentQueue.value = QueueCache(1, isDelay = false, isCurrentQueue = true)
    }

    fun logout() {
        _homeState.value = HomeState.Logout
    }

    fun homeStateOnIdle() {
        _homeState.value = CommonState.Idle
    }

    fun homeDialogStateIdle() {
        _homeDialogState.value = HomeDialogState.Idle
    }

    fun sync() {
        viewModelScope.launch(dispatcher) {
            _homeState.postValue(HomeState.OnSync)
            delay(2000)
            randomCounter()
            lastSync.postValue("Last sync: ${"".getLastSync()}")
            val random = Random.nextInt(2, 50)
            val t = random.div(2)
            for (i in 2 until t) {
                waitings[i.toLong()] =
                    QueueCache(i.toLong(), isDelay = false, isCurrentQueue = false)
            }
            for (i in t until random) {
                delays[i.toLong()] = QueueCache(i.toLong(), isDelay = true, isCurrentQueue = false)
            }
            queueWaiting.postValue(waitings.values.toList())
            queueDelay.postValue(delays.values.toList())
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

    private fun doRestoreQueue(item: QueueCache) {
        viewModelScope.launch(dispatcher) {
            val tempCurrentQueue =
                if (mTempCurrentQueue.number > 0) mTempCurrentQueue.number else currentQueue.value?.number
                    ?: -1
        }
    }

    private fun skipCurrentQueue() {
        viewModelScope.launch(dispatcher) {
            val tempCurrentQueue = currentQueue.value
            if (tempCurrentQueue != null) {

            } else {
                _homeState.postValue(HomeState.CurrentQueueIsEmpty)
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

    fun restoreQueue(item: QueueCache) {
        _homeDialogState.value = HomeDialogState.RestoreQueue(item)
    }

    fun skipCurrentQueue(item: QueueCache) {
        _homeDialogState.value = HomeDialogState.SkipCurrentQueue(item)
    }

    fun successCurrentQueue(queue: String) {
        _homeDialogState.value = HomeDialogState.SuccessCurrentQueue(queue)
    }

    fun actionSearch() {
        when {
            searchQueue.value.isNullOrEmpty() -> {
                _homeState.value = HomeState.ParamSearchQueueEmpty
            }
            searchQueue.value != null -> {
                searchQueue.value?.let { param ->
                    if (param.length < 2) {
                        _homeState.value = HomeState.ParamSearchQueueLessThanTwo
                    } else {
                        // dummyData
                        currentQueue.value?.let {
                            val searchParam = "${subLocationInitial.value}$param"
                            val result = if (it.getQueue() == searchParam) {
                                it
                            } else {
                                val delay = delays[param.toLong()]
                                val waiting = waitings[param.toLong()]
                                delay ?: waiting
                            }
                            if (result == null) {
                                _homeState.postValue(HomeState.SearchResultIsEmpty)
                            } else {
                                replaceTempCurrentQueue(result)
                                _homeState.postValue(HomeState.ShowSearchResult)
                            }
                        }

                    }
                }
            }
        }
    }


    fun onSearchTextChanged(text: CharSequence) {
        if (text.isEmpty()) {
            _homeState.value = CommonState.Idle
            rollBackTempCurrent()
        }
    }

    fun clearSearchQueue() {
        searchQueue.value = ""
    }

    fun submitBottomSheet(action: Action, item: QueueCache?) {
        when (action) {
            Action.SKIP -> {
                item?.let {
                    skipCurrentQueue()
                }
            }
            Action.LOGOUT -> {
                doLogout()
            }
            Action.RESTORE -> {
                item?.let {
                    doRestoreQueue(it)
                }
            }
        }
    }

    fun submitRitaseDialog() {
        viewModelScope.launch {
            taxiNumber.value = ""
        }
    }

    fun dummyIndicator() {
        _homeState.value = HomeState.DummyIndicator
    }

    fun changeIndicator(isConnected: Boolean) {
        connectionState.value = isConnected
        if (isConnected.not()) {
            _homeState.value = CommonState.ConnectionNotFound
        }
    }

    private fun doLogout() {
        viewModelScope.launch(dispatcher) {
            logoutCases.invoke(UserUtils.getUUID()).collect {
                _homeState.postValue(HomeState.LogoutSuccess)
            }
        }
    }

    private fun sortQueue(queueMap: HashMap<Long, QueueCache>): List<QueueCache> {
        return if (queueMap.isEmpty()) {
            ArrayList()
        } else {
            val list = queueMap.values.toList()
            return list.sortedBy { it.number }
        }
    }

    private fun rollBackTempCurrent() {
        if (mTempCurrentQueue.number > 0) {
            currentQueue.postValue(mTempCurrentQueue)
            mTempCurrentQueue = QueueCache()
        }
    }

    private fun replaceTempCurrentQueue(queueCache: QueueCache) {
        if (!queueCache.isCurrentQueue) {
            mTempCurrentQueue = currentQueue.value ?: QueueCache()
            currentQueue.value = queueCache
        }
    }


    fun dialogQueueReceipt() {
        _homeState.value = HomeState.DialogQueueReceipt
    }
}