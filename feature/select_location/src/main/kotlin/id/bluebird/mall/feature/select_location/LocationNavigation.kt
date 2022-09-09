package id.bluebird.mall.feature.select_location

import id.bluebird.mall.feature.select_location.model.LocationNavigation

object LocationNavigationTemporary {

    private var _locationNav: LocationNavigation? = null

    fun updateLocationNav(locationNavigation: LocationNavigation?) {
        _locationNav = locationNavigation
    }

    fun removeTempData() {
        _locationNav = null
    }

    fun getLocationNav(): LocationNavigation? = _locationNav

    fun isLocationNavAvailable(): Boolean = _locationNav != null
}