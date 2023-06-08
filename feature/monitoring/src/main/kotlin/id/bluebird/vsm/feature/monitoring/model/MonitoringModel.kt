package id.bluebird.vsm.feature.monitoring.model

import androidx.annotation.Keep


@Keep
data class MonitoringModel(
    val subLocationId: Long,
    val locationName: String,
    val subLocationName: String,
    val fleetCount: Int,
    val queueCount: Int,
    val totalFleetCount: Int,
    val totalQueueCount: Int,
    val totalRitase: Int,
    val fleetRequest: Int,
    val buffer: Int,
    val editableBuffer: Boolean,
)
