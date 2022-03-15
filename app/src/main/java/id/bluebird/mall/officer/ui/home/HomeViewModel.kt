package id.bluebird.mall.officer.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.utils.DateUtils

class HomeViewModel : ViewModel() {
    val connectionState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _homeState: MutableLiveData<CommonState> = MutableLiveData()
    var homeState = _homeState

    val locationName: MutableLiveData<String> = MutableLiveData()
    val subLocationName: MutableLiveData<String> = MutableLiveData("Lobby Utara")

    init {
        locationName.value = "Gandaria City, ${DateUtils.getTodayDate()}"
    }

    fun logout() {
        homeState.value = HomeState.Logout
    }
}