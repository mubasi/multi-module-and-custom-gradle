package id.bluebird.vsm.domain.fleet.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class MonitoringData(
    var buffer: Int = 0,
    @get:PropertyName("location_name")
    @set:PropertyName("location_name")
    var locationName: String = "",
    @get:PropertyName("sub_location_name")
    @set:PropertyName("sub_location_name")
    var subLocationName: String = "",
    @get:PropertyName("queue_fleet")
    @set:PropertyName("queue_fleet")
    var queueFleet: Int = 0,
    @get:PropertyName("queue_passenger")
    @set:PropertyName("queue_passenger")
    var queuePassenger: Int = 0,
    var request: Int = 0,
    @get:PropertyName("sub_location_id")
    @set:PropertyName("sub_location_id")
    var subLocationId: Long = 0,
    @get:PropertyName("total_queue_fleet")
    @set:PropertyName("total_queue_fleet")
    var totalQueueFleet: Int = 0,
    @get:PropertyName("total_queue_passenger")
    @set:PropertyName("total_queue_passenger")
    var totalQueuePassenger: Int = 0,
    @get:PropertyName("total_ritase")
    @set:PropertyName("total_ritase")
    var totalRitase: Int = 0
)