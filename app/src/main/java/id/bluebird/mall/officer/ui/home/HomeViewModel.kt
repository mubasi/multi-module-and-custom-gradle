package id.bluebird.mall.officer.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.officer.case.queue.RestoreQueueCases
import id.bluebird.mall.officer.case.queue.SkipQueueCases
import id.bluebird.mall.officer.case.user.LogoutCases
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.ui.home.dialog.Action
import id.bluebird.mall.officer.ui.home.model.CounterModel
import id.bluebird.mall.officer.ui.home.model.QueueCache
import id.bluebird.mall.officer.utils.DateUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

class HomeViewModel(
    private val skipQueueCases: SkipQueueCases,
    private val restoreQueueCases: RestoreQueueCases,
    private val logoutCases: LogoutCases,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    ViewModel() {

    companion object {
        const val MAX_TIMER = 30
    }

    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<CommonState> = MutableLiveData()
    var homeState = _homeState

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

    init {
        locationName.value = "Gandaria City, ${DateUtils.getTodayDate()}"
        currentQueue.value = QueueCache(999, isDelay = false, isCurrentQueue = true)
    }

    fun logout() {
        _homeState.value = HomeState.Logout
    }

    fun homeStateOnIdle() {
        _homeState.value = CommonState.Idle
    }

    fun sync() {
        viewModelScope.launch(dispatcher) {
            _homeState.postValue(HomeState.OnSync)
            delay(2000)
            randomCounter()
            lastSync.postValue("Last sync: ${DateUtils.getLastSycnFormat()}")
            val random = Random.nextInt(1, 31)
            val t = random.div(2)
            for (i in 1 until t) {
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

    private fun skipCurrentQueue() {
        viewModelScope.launch(dispatcher) {
            skipQueueCases.invoke(currentQueue.value, waitings, delays)
                .catch { cause: Throwable ->
                    _homeState.postValue(CommonState.Error(cause))
                }
                .collectLatest {
                    currentQueue.postValue(it.currentQueue)
                    delays.putAll(it.delayQueue)
                    queueDelay.postValue(delays.values.toList())
                    waitings.putAll(it.waitingQueue)
                    queueWaiting.postValue(waitings.values.toList())
                    if (it.currentQueue != null) {
                        _homeState.postValue(HomeState.SuccessSkiped(it.currentQueue.getQueue()))
                    }
                }
        }
    }

    fun restoreQueue(item: QueueCache) {
        viewModelScope.launch(dispatcher) {
            restoreQueueCases.invoke(currentQueue.value, item, waitings, delays)
                .catch { cause: Throwable ->
                    _homeState.postValue(CommonState.Error(cause))
                }.collectLatest {
                    currentQueue.postValue(it.currentQueue)
                    delays.putAll(it.delayQueue)
                    queueDelay.postValue(delays.values.toList())
                    waitings.putAll(it.waitingQueue)
                    queueWaiting.postValue(waitings.values.toList())
                    if (it.currentQueue != null) {
                        _homeState.postValue(HomeState.SuccessSkiped(it.currentQueue.getQueue()))
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

    fun submitBottomSheet(action: Action, item: QueueCache?) {
        when (action) {
            Action.SKIP -> {
                item?.let {
                    skipCurrentQueue()
                    _homeState.value = HomeState.SuccessSkiped(item.getQueue())
                }
            }
            Action.LOGOUT -> {
                doLogout()
            }
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

    private fun doLogout() {
        viewModelScope.launch(dispatcher) {
            logoutCases.invoke().collectLatest {
                _homeState.postValue(HomeState.LogoutSuccess)
            }
        }
    }
}