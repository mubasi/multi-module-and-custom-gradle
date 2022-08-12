package id.bluebird.mall.feature_monitoring.main

import id.bluebird.mall.feature_monitoring.model.MonitoringModel

sealed class MonitoringState {
    object OnProgressGetList: MonitoringState()
    object OnFailedGetList: MonitoringState()
    object OnSuccessSaveBuffer: MonitoringState()
    data class OnFailedSaveBuffer(val message: String): MonitoringState()
    data class OnSuccessGetList(val data: List<MonitoringModel>): MonitoringState()
    data class RequestEditBuffer(val item: MonitoringModel?): MonitoringState()
}
