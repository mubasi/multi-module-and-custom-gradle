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

class HomeViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    ViewModel() {
    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<CommonState> = MutableLiveData()
    var homeState = _homeState

    val locationName: MutableLiveData<String> = MutableLiveData()
    val lastSync: MutableLiveData<String> = MutableLiveData("Last sync : 11 Jan 2022 • 13:48")
    val subLocationName: MutableLiveData<String> = MutableLiveData("Lobby Utara")

    init {
        locationName.value = "Gandaria City, ${DateUtils.getTodayDate()}"
    }

    fun logout() {
        _homeState.value = HomeState.Logout
    }

    fun sync() {
        viewModelScope.launch(dispatcher) {
            _homeState.postValue(HomeState.OnSync)
            delay(2000)
            lastSync.postValue("Last sync: ${DateUtils.getLastSycnFormat()}")
            _homeState.postValue(CommonState.Idle)
        }
    }
}