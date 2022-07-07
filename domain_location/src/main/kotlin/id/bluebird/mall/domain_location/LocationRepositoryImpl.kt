package id.bluebird.mall.domain_location

import id.bluebird.mall.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.LocationPangkalanGrpc
import proto.LocationPangkalanOuterClass

class LocationRepositoryImpl(
    private val locationGrpc: LocationPangkalanGrpc.LocationPangkalanBlockingStub = LocationPangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : LocationRepository {
    override fun getSubLocationByLocationId(locationId: Long): Flow<LocationPangkalanOuterClass.GetSubLocationByLocationResp> =
        flow {
            val request = LocationPangkalanOuterClass.GetSubLocationByLocationReq.newBuilder()
                .apply {
                    this.locationId = locationId
                }
                .build()
            val response = locationGrpc.getSubLocationByLocation(request)
            emit(response)

        }
}