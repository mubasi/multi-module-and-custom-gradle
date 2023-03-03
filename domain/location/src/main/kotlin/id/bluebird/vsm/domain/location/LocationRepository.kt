package id.bluebird.vsm.domain.location

import kotlinx.coroutines.flow.Flow
import proto.LocationPangkalanOuterClass


interface LocationRepository {
    fun getSubLocations(): Flow<LocationPangkalanOuterClass.GetSubLocationsResponse>
    fun getSubLocationByLocationId(locationId: Long): Flow<LocationPangkalanOuterClass.GetSubLocationByLocationResp>
    fun updateBuffer(
        subLocationId: Long,
        value: Long
    ): Flow<LocationPangkalanOuterClass.ResponseUpdateBuffer>

    fun getLocations(): Flow<LocationPangkalanOuterClass.GetLocationsResponse>

    fun getSubLocationQrCode(
        subLocationId: Long
    ): Flow<LocationPangkalanOuterClass.ResponseGetLocationQrCode>
}