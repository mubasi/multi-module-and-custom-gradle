package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.MonitoringResultState
import kotlinx.coroutines.flow.Flow

interface Monitoring {
    fun invoke(): Flow<MonitoringResultState>
}