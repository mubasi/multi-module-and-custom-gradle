package id.bluebird.vsm.domain.location.model

data class GetLocationQrCodeResult(
    val subLocationId: Long = -1,
    val locationId: Long = -1,
    val subLocationName: String = "",
    val daQrCode: String = "",
    val queuePassengerQrCode: String = ""
)