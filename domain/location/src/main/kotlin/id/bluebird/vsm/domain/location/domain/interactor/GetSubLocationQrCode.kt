package id.bluebird.vsm.domain.location.domain.interactor

import id.bluebird.vsm.domain.location.GetLocationQrCodeState
import kotlinx.coroutines.flow.Flow

interface GetSubLocationQrCode {
    operator fun invoke(
        subLocationId: Long
    ): Flow<GetLocationQrCodeState>
}