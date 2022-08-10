package id.bluebird.mall.domain_fleet.model

import androidx.annotation.Keep

@Keep
data class MonitoringData(
    var buffer: Int = 0,
    var location_name: String = "",
    var queue_fleet: Int = 0,
    var queue_passenger: Int = 0,
    var request: Int = 0,
    var sub_location_id: Long = 0,
    var total_queue_fleet: Int = 0,
    var total_queue_passenger: Int = 0,
    var total_ritase: Int = 0
)