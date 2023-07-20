package id.multi.module.custome.navigation

import androidx.fragment.app.Fragment

sealed class NavigationSealed(
    val action: Int? = null,
    val fragment: Fragment
) {
    data class ProfilePage(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)
    data class HomePage(val destination: Int? = null, val frag: Fragment) :
        NavigationSealed(action = destination, fragment = frag)
}