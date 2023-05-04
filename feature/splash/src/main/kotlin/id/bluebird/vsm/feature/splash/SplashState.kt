package id.bluebird.vsm.feature.splash

sealed class SplashState {
    object Login : SplashState()
    object LoginAsOutletUser : SplashState()
    object LoginAsAirportUser : SplashState()
    data class DoUpdateVersion(val url: String, val versionName: String) : SplashState()
}
