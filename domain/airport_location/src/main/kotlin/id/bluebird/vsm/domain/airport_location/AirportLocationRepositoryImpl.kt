package id.bluebird.vsm.domain.airport_location

import com.google.protobuf.Empty
import id.bluebird.vsm.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.LocationGrpc
import proto.LocationOuterClass

class AirportLocationRepositoryImpl(
    private val locationAirportGrpc : LocationGrpc.LocationBlockingStub = LocationGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : AirportLocationRepository {
    override fun getSubLocationByLocationIdAirport(
        locationId: Long,
        showDeposition: Boolean,
        showWingsChild: Boolean
    ): Flow<LocationOuterClass.GetSubLocationByLocationResp> = flow {
        val request = LocationOuterClass.GetSubLocationByLocationReq .newBuilder()
            .apply {
                this.locationId = locationId
                this.filter = LocationOuterClass.Filter.newBuilder()
                    .apply {
                        this.showDeposition = showDeposition
                        this.showWingsChild = showWingsChild
                    }.build()
            }.build()
        val response = locationAirportGrpc.getSubLocationByLocation(request)
        emit(response)
    }

    override fun getLocationAirport(): Flow<LocationOuterClass.GetLocationsResponse> = flow {
        val response = locationAirportGrpc.getLocations(Empty.getDefaultInstance())
        emit(response)
    }
}