package id.bluebird.vsm.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

object NavigationNav {

    fun navigate(navigationSealed: NavigationSealed) {
        val uri = getNavigateUri(navigationSealed)
        val request = NavDeepLinkRequest.Builder
            .fromUri(uri)
            .build()
        val navOptions = getNavOptions(navigationSealed.action)
        findNavController(navigationSealed.fragment).navigate(request, navOptions = navOptions)
    }

    private fun getNavigateUri(navigationSealed: NavigationSealed): Uri {
        val target = when (navigationSealed) {
            is NavigationSealed.Login -> "login"
            is NavigationSealed.Splash -> "splash"
            is NavigationSealed.QueueFleet -> "queue_fleet"
            is NavigationSealed.QueuePassenger -> "queue_passenger"
            is NavigationSealed.Monitoring -> "monitoring_nav"
            is NavigationSealed.SelectLocation -> "select_location/${navigationSealed.isMenuFleet}"
            is NavigationSealed.QrCode -> "qr_code/${navigationSealed.locationId}/${navigationSealed.subLocationId}/${navigationSealed.titleLocation}/${navigationSealed.position}"
        }
        return "android-app://id.bluebird.mall/$target".toUri()
    }

    private fun getNavOptions(action: Int?): NavOptions? =
        if (action == null) null else NavOptions.Builder()
            .setPopUpTo(destinationId = action, inclusive = true, saveState = false).build()

}