package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.CounterBarState
import kotlinx.coroutines.flow.Flow

interface CounterBar {
    operator fun invoke (
        locationId: Long,
    ) : Flow<CounterBarState>
}