package id.bluebird.vsm.domain.location

import com.google.protobuf.Empty
import id.bluebird.vsm.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.LocationPangkalanGrpc
import proto.LocationPangkalanOuterClass

class LocationRepositoryImpl(
    private val locationGrpc: LocationPangkalanGrpc.LocationPangkalanBlockingStub = LocationPangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : LocationRepository {
    override fun getSubLocations(): Flow<LocationPangkalanOuterClass.GetSubLocationsResponse> =
        flow {
            val response = locationGrpc.getSubLocations(Empty.getDefaultInstance())
            emit(response)
        }

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

    override fun updateBuffer(
        subLocationId: Long,
        value: Long
    ): Flow<LocationPangkalanOuterClass.ResponseUpdateBuffer> =
        flow {
            val request = LocationPangkalanOuterClass.RequestUpdateBuffer.newBuilder()
                .apply {
                    this.subLocationId = subLocationId
                    this.value = value
                }
                .build()

            val response = locationGrpc.updateBufferSubLocation(request)
            emit(response)
        }

    override fun getLocations(): Flow<LocationPangkalanOuterClass.GetLocationsResponse> =
        flow {
            val request = Empty.newBuilder().build()
            val response = locationGrpc.getLocations(request)

            emit(response)
        }
}