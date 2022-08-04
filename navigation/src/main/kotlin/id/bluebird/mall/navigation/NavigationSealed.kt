package id.bluebird.mall.navigation

import androidx.fragment.app.Fragment

sealed class NavigationSealed(
    val action: Int? = null,
    val fragment: Fragment
) {
    data class Home(val destination: Int?, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)

    data class Login(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)

    data class Splash(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)

    data class QueueFleet(
        val destination: Int? = null,
        val frag: Fragment
    ) :
        NavigationSealed(action = destination, fragment = frag)

    data class QueuePassenger(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)
}