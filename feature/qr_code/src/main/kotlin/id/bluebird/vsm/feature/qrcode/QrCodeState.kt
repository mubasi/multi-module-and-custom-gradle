package id.bluebird.vsm.feature.qrcode

sealed class QrCodeState {
    object Progress : QrCodeState()
    data class SuccessLoad(
        val image: String
    ) : QrCodeState()

    data class OnError(
        val err: Throwable
    ) : QrCodeState()
}