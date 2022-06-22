package id.bluebird.mall.splash

sealed class SplashState {
    object Login : SplashState()
    object Home : SplashState()
}
