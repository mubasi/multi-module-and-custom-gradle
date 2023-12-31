package id.multi.module.custome.navigation

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
            is NavigationSealed.ProfilePage -> "profilepage"
            is NavigationSealed.HomePage -> "homepage"
        }
        return "android-app://id.modularization.custom/$target".toUri()
    }

    private fun getNavOptions(action: Int?): NavOptions? =
        if (action == null) null else NavOptions.Builder()
            .setPopUpTo(destinationId = action, inclusive = true, saveState = false).build()

}