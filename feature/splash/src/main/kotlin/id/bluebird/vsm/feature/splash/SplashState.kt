package id.bluebird.vsm.feature.splash

sealed class SplashState {
    object Login : SplashState()
    object Home : SplashState()
    data class DoUpdateVersion(val url: String, val versionName: String) : SplashState()
}
