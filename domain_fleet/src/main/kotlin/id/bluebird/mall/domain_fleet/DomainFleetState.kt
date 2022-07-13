package id.bluebird.mall.domain_fleet

import id.bluebird.mall.domain_fleet.model.CountResult

sealed class GetCountState {
    data class Success(val countResult: CountResult) : GetCountState()
}

sealed class RequestState {
    data class Success(val count: Long) : RequestState()
    object CountInvalid : RequestState()
    object SubLocationInvalid : RequestState()
}