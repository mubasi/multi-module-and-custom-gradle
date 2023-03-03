package id.bluebird.vsm.domain.location.domain.cases

import id.bluebird.vsm.domain.location.GetLocationQrCodeState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationQrCode
import id.bluebird.vsm.domain.location.model.GetLocationQrCodeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetSubLocationQrCodeCases(
    private val locationRepository: LocationRepository
) : GetSubLocationQrCode {
    override fun invoke(subLocationId: Long): Flow<GetLocationQrCodeState> = flow {
        val response = locationRepository.getSubLocationQrCode(
            subLocationId = subLocationId
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        emit(
            GetLocationQrCodeState.Success(
                GetLocationQrCodeResult(
                    subLocationId = response.subLocationId,
                    locationId = response.locationId,
                    subLocationName = response.subLocationName,
                    daQrCode = response.daQrCode,
                    queuePassengerQrCode = response.queuePassangerQrCode
                )
            )
        )
    }
}