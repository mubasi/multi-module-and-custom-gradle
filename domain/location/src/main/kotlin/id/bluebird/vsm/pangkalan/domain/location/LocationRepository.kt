package id.bluebird.vsm.domain.location

import kotlinx.coroutines.flow.Flow
import proto.OutletLocationPangkalanOuterClass


interface LocationRepository {
    fun getSubLocations(): Flow<OutletLocationPangkalanOuterClass.GetSubLocationsPangkalanResponse>
    fun getSubLocationByLocationId(locationId: Long): Flow<OutletLocationPangkalanOuterClass.GetSubLocationPangkalanByLocationResp>
    fun updateBuffer(
        subLocationId: Long,
        value: Long
    ): Flow<OutletLocationPangkalanOuterClass.ResponseUpdateBufferPangkalan>

    fun getLocations(): Flow<OutletLocationPangkalanOuterClass.GetLocationsPangkalanResponse>

    fun getSubLocationQrCode(
        subLocationId: Long
    ): Flow<OutletLocationPangkalanOuterClass.ResponseGetLocationPangkalanQrCode>
}