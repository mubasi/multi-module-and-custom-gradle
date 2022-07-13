package id.bluebird.mall.feature_queue_fleet.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserById
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.feature_queue_fleet.model.CountCache
import id.bluebird.mall.feature_queue_fleet.model.UserInfo
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class QueueFleetViewModel(
    private val getCount: GetCount,
    private val getUserById: GetUserById
) : ViewModel() {

    companion object {
        internal const val ERROR_MESSAGE_UNKNOWN = "Unknown"
        internal const val ERROR_USER_ID = "User is wrong"
    }

    val isPerimeter: MutableLiveData<Boolean> = MutableLiveData()
    val counterLiveData: MutableLiveData<CountCache> = MutableLiveData()
    private val _queueFleetState: MutableSharedFlow<QueueFleetState> =
        MutableSharedFlow()
    val queueFleetState: SharedFlow<QueueFleetState> = _queueFleetState.asSharedFlow()

    private var mCountCache: CountCache = CountCache()
    private lateinit var mUserInfo: UserInfo

    @VisibleForTesting
    fun setUserInfo(userInfo: UserInfo) {
        mUserInfo = userInfo
    }

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
            _queueFleetState.emit(QueueFleetState.ProgressGetUser)
            getUserById.invoke(mUserInfo.userId)
                .catch { cause ->
                    _queueFleetState.emit(
                        QueueFleetState.FailedGetUser(cause.message ?: ERROR_MESSAGE_UNKNOWN)
                    )
                }
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            mUserInfo.locationId = it.result.locationId
                            mUserInfo.subLocationId = it.result.subLocationsId.first()
                            _queueFleetState.emit(QueueFleetState.GetUserInfoSuccess)
                        }
                        GetUserByIdState.UserIdIsWrong -> {
                            _queueFleetState.emit(QueueFleetState.FailedGetUser(ERROR_USER_ID))
                        }
                    }
                }
        }
    }

    fun getCounter() {
        viewModelScope.launch {
            getCount.invoke(mUserInfo.subLocationId)
                .catch { cause ->
                    _queueFleetState.emit(
                        QueueFleetState.FailedGetCounter(cause.message ?: ERROR_MESSAGE_UNKNOWN)
                    )
                }
                .collect {
                    when (it) {
                        is GetCountState.Success -> {
                            it.countResult.let { result ->
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

    fun updateRequestCount(count: Long) {
        if (count >= RequestFleetDialogViewModel.MINIMUM_COUNTER_VALUE) {
            mCountCache.request = count
            counterLiveData.value = mCountCache
        }
    }

    fun showRequestFleet() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ShowRequestFleet(mUserInfo.subLocationId))
        }
    }
}