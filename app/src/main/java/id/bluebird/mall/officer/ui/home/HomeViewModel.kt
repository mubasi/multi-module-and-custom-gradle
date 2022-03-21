package id.bluebird.mall.officer.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.utils.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    ViewModel() {
    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<CommonState> = MutableLiveData()
    var homeState = _homeState

    val searchQueue: MutableLiveData<String> = MutableLiveData()
    val locationName: MutableLiveData<String> = MutableLiveData()
    val lastSync: MutableLiveData<String> = MutableLiveData("Last sync : 11 Jan 2022 • 13:48")
    val subLocationName: MutableLiveData<String> = MutableLiveData("Lobby Utara")
    val counter: MutableLiveData<CounterModel> = MutableLiveData(CounterModel())

    init {
        locationName.value = "Gandaria City, ${DateUtils.getTodayDate()}"
        randomCounter()
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
                    }
                }
            }
        }
    }

    fun dummyIndicator() {
        _homeState.value = HomeState.DummyIndicator
    }

    fun changeIndicator(isConnected: Boolean) {
        connectionState.value = isConnected
    }
}