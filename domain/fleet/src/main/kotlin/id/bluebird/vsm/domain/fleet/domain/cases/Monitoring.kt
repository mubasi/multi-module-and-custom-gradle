package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.MonitoringResultState
import kotlinx.coroutines.flow.Flow

interface Monitoring {
    fun invoke(): Flow<MonitoringResultState>
}