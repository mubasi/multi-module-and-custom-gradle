package id.bluebird.mall.domain_location

import kotlinx.coroutines.flow.Flow
import proto.LocationPangkalanOuterClass


interface LocationRepository {
    fun getSubLocationByLocationId(locationId: Long): Flow<LocationPangkalanOuterClass.GetSubLocationByLocationResp>
}