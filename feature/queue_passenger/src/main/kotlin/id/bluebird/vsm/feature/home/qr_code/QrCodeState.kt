package id.bluebird.vsm.feature.home.qr_code

sealed class QrCodeState {
    object Progress : QrCodeState()
    data class SuccessLoad(
        val image: String
    ) : QrCodeState()

    data class OnError(
        val err: Throwable
    ) : QrCodeState()
}