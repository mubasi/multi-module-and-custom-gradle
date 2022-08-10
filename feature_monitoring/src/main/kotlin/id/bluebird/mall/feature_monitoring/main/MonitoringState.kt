package id.bluebird.mall.feature_monitoring.main

import id.bluebird.mall.feature_monitoring.model.MonitoringModel

sealed class MonitoringState {
    object OnProgressGetList: MonitoringState()
    object OnFailedGetList: MonitoringState()
    data class OnSuccessGetList(val data: List<MonitoringModel>): MonitoringState()
}
