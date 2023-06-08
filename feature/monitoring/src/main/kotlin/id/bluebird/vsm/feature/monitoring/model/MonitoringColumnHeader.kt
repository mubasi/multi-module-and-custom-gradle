package id.bluebird.vsm.feature.monitoring.model

import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel

data class MonitoringColumnHeader(
    val data: String,
    val columnIndex : Int,
    val order : MonitoringViewModel.ActiveSort,
    val isDesc : Boolean
)
