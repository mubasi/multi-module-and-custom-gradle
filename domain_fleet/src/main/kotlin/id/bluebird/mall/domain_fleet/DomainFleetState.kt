package id.bluebird.mall.domain_fleet

import id.bluebird.mall.domain_fleet.model.CountResult

sealed class GetCountState {
    data class Success(val countResult: CountResult) : GetCountState()
}