package id.bluebird.vsm.domain.location

import com.google.protobuf.Empty
import id.multi.module.custome.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.OutletLocationPangkalanGrpc
import proto.OutletLocationPangkalanOuterClass

class LocationRepositoryImpl(
    private val locationGrpc: OutletLocationPangkalanGrpc.OutletLocationPangkalanBlockingStub = OutletLocationPangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : LocationRepository {
    override fun getSubLocations(): Flow<OutletLocationPangkalanOuterClass.GetSubLocationsPangkalanResponse> =
        flow {
            val response = locationGrpc.getSubLocationsPangkalan(Empty.getDefaultInstance())
            emit(response)
        }

    override fun getSubLocationByLocationId(locationId: Long): Flow<OutletLocationPangkalanOuterClass.GetSubLocationPangkalanByLocationResp> =
        flow {
            val request = OutletLocationPangkalanOuterClass.GetSubLocationPangkalanByLocationReq.newBuilder()
                .apply {
                    this.locationId = locationId
                }
                .build()
            val response = locationGrpc.getSubLocationPangkalanByLocation(request)
            emit(response)

        }

    override fun updateBuffer(
        subLocationId: Long,
        value: Long
    ): Flow<OutletLocationPangkalanOuterClass.ResponseUpdateBufferPangkalan> =
        flow {
            val request = OutletLocationPangkalanOuterClass.RequestUpdateBufferPangkalan.newBuilder()
                .apply {
                    this.subLocationId = subLocationId
                    this.value = value
                }
                .build()

            val response = locationGrpc.updateBufferSubLocationPangkalan(request)
            emit(response)
        }

    override fun getLocations(): Flow<OutletLocationPangkalanOuterClass.GetLocationsPangkalanResponse> =
        flow {
            val request = Empty.newBuilder().build()
            val response = locationGrpc.getLocationsPangkalan(request)

            emit(response)
        }

    override fun getSubLocationQrCode(
        subLocationId: Long
    ): Flow<OutletLocationPangkalanOuterClass.ResponseGetLocationPangkalanQrCode> = flow {
        val request = OutletLocationPangkalanOuterClass.RequestGetLocationPangkalanQrCode.newBuilder()
            .apply {
                this.subLocationId = subLocationId
            }
            .build()
        val response = locationGrpc.getLocationPangkalanQrCode(request)
        emit(response)
    }
}