package id.bluebird.vsm.feature.splash

sealed class SplashState {
    object Login : SplashState()
    object Home : SplashState()
}
