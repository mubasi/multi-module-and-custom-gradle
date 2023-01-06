package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.CounterBarState
import kotlinx.coroutines.flow.Flow

interface CounterBar {
    operator fun invoke (
        locationId: Long,
        subLocationId: Long,
    ) : Flow<CounterBarState>
}