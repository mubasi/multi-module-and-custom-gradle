package id.bluebird.mall.feature_queue_fleet.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserDomainState
import id.bluebird.mall.domain.user.domain.intercator.GetUserById
import id.bluebird.mall.domain_fleet.DomainFleetState
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.feature_queue_fleet.model.CountCache
import id.bluebird.mall.feature_queue_fleet.model.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class QueueFleetViewModel(
    private val getCount: GetCount,
    private val getUserById: GetUserById
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    val isPerimeter: MutableLiveData<Boolean> = MutableLiveData()
    val counterLiveData: MutableLiveData<CountCache> = MutableLiveData()
    private val _queueFleetState = MutableStateFlow<QueueFleetState>(QueueFleetState.Idle)
    val queueFleetState: StateFlow<QueueFleetState> = _queueFleetState

    private var mCountCache: CountCache = CountCache()
    private lateinit var mUserInfo: UserInfo

    fun initUserId(userId: Long?) {
        mUserInfo = if (userId == null) {
            UserInfo(UserUtils.getUserId())
        } else {
            UserInfo(userId)
        }
        getUserById()
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queueFleetState.value = QueueFleetState.ProgressGetUser
            getUserById.invoke(mUserInfo.userId)
                .catch { cause ->
                    _queueFleetState.value =
                        QueueFleetState.FailedGetUser(cause.message ?: ERROR_MESSAGE_UNKNOWN)
                }
                .collect {
                    when (it) {
                        is UserDomainState.Success -> {
                            mUserInfo.locationId = it.value.locationId
                            mUserInfo.subLocationId = it.value.subLocationsId.first()
                            _queueFleetState.value = QueueFleetState.GetUserInfoSuccess
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
        }
    }

    fun getCounter() {
        viewModelScope.launch {
            getCount.invoke(mUserInfo.subLocationId)
                .catch { cause ->
                    _queueFleetState.value =
                        QueueFleetState.FailedGetCounter(cause.message ?: ERROR_MESSAGE_UNKNOWN)
                }
                .collect {
                    when (it) {
                        is DomainFleetState.Success -> {
                            it.value.let { result ->
                                mCountCache = CountCache(
                                    stock = result.stock,
                                    request = result.request,
                                    ritase = result.ritase
                                )
                                counterLiveData.postValue(mCountCache)
                            }
                        }
                    }

                }
        }
    }
}