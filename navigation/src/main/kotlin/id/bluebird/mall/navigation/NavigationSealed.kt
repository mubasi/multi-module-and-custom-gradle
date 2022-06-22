package id.bluebird.mall.navigation

import androidx.fragment.app.Fragment

sealed class NavigationSealed(val fragment: Fragment) {
    data class Home(val f: Fragment) : NavigationSealed(fragment = f)
    data class Login(val f: Fragment) : NavigationSealed(fragment = f)
    data class Splash(val f: Fragment) : NavigationSealed(fragment = f)
}