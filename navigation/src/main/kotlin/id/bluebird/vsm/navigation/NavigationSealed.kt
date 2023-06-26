package id.bluebird.vsm.navigation

import androidx.fragment.app.Fragment

sealed class NavigationSealed(
    val action: Int? = null,
    val fragment: Fragment
) {
    data class Splash(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)
}