package id.bluebird.vsm.feature.monitoring.filter_dialog

import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel

sealed class FilterDialogState {
    object CloseFilter : FilterDialogState()
    data class SaveFilter(
        val result : MonitoringViewModel.FilterStatus
    ) : FilterDialogState()
}