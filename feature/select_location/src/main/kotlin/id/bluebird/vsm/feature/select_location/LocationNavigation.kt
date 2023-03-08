package id.bluebird.vsm.feature.select_location

import androidx.annotation.VisibleForTesting
import id.bluebird.vsm.core.extensions.StringExtensions
import id.bluebird.vsm.feature.select_location.model.LocationNavigation

object LocationNavigationTemporary {

    private var _locationNav: LocationNavigation? = null
    private var isOnTesting = false
    @VisibleForTesting
    const val locationName = "sample"
    @VisibleForTesting
    const val subLocationName = "sub ${StringExtensions.SUFFIX_TEST}"

    fun updateLocationNav(locationNavigation: LocationNavigation?) {
        _locationNav = locationNavigation
    }

    fun removeTempData() {
        _locationNav = null
    }

    fun getLocationNav(): LocationNavigation? {
        return if (isOnTesting) {
            LocationNavigation(
                locationName = "sample"
            )
        } else {
            _locationNav
        }
    }

    fun isLocationNavAvailable(): Boolean {
        return if(isOnTesting){
            return false
        } else {
            _locationNav != null
        }
    }

    @VisibleForTesting
    fun setTestingVariable(isOnTesting : Boolean){
        this.isOnTesting = isOnTesting
    }
}