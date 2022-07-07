package id.bluebird.mall.navigation

import androidx.fragment.app.Fragment

sealed class NavigationSealed(val action: Int?, val fragment: Fragment) {
    data class Home(val destination: Int?, val f: Fragment) :
        NavigationSealed(action = destination, fragment = f)

    data class Login(val destination: Int?, val f: Fragment) :
        NavigationSealed(action = destination, fragment = f)

    data class Splash(val destination: Int?, val f: Fragment) :
        NavigationSealed(action = destination, fragment = f)
}