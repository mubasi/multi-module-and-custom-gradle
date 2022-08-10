package id.bluebird.mall.feature_monitoring.model

data class MonitoringModel(
    val subLocationId: Long,
    val locationName: String,
    val fleetCount: Int,
    val queueCount: Int,
    val totalFleetCount: Int,
    val totalQueueCount: Int,
    val totalRitase: Int,
    val fleetRequest: Int,
    val buffer: Int,
)
