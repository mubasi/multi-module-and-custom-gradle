package id.bluebird.mall.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

object NavigationNav {

    fun navigate(navigationSealed: NavigationSealed) {
        val uri = getNavigateUri(navigationSealed)
        val request = NavDeepLinkRequest.Builder
            .fromUri(uri)
            .build()
        findNavController(navigationSealed.fragment).navigate(request)
    }

    private fun getNavigateUri(navigationSealed: NavigationSealed): Uri {
        val target = when (navigationSealed) {
            is NavigationSealed.Home -> "home"
            is NavigationSealed.Login -> "login"
            is NavigationSealed.Splash -> "splash"
        }
        return "android-app://id.bluebird.mall/$target".toUri()
    }
}