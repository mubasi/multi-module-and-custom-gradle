package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.GetListFleetTerminalDepartState
import kotlinx.coroutines.flow.Flow

interface GetListFleetTerminal {
    operator fun invoke(
        subLocationId: Long,
        page: Int,
        itemPerPage: Int
    ) : Flow<GetListFleetTerminalDepartState>
}