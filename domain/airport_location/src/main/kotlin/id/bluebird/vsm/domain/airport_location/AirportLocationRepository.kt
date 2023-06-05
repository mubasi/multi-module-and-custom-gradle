package id.bluebird.vsm.domain.airport_location

import kotlinx.coroutines.flow.Flow
import proto.LocationOuterClass

interface AirportLocationRepository {
    fun getSubLocationByLocationIdAirport(
        locationId:Long, showDeposition: Boolean = false, showWingsChild: Boolean = false
    ) : Flow<LocationOuterClass.GetSubLocationByLocationResp>
    fun getLocationAirport() : Flow<LocationOuterClass.GetLocationsResponse>
}