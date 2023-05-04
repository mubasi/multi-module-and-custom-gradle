package id.bluebird.vsm.feature.airport_fleet.utils

sealed class EmptyType {
    object FilterFleet : EmptyType()
    object Terminal : EmptyType()
    object Perimeter : EmptyType()
}
