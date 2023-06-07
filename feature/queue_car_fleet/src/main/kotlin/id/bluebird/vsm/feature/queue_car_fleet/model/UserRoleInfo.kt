package id.bluebird.vsm.feature.queue_car_fleet.model

import androidx.annotation.Keep

@Keep
data class UserRoleInfo(val userId: Long = 0, var locationId: Long = -1, var subLocationId: Long = -1)