package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.GetUserAssignmentState
import kotlinx.coroutines.flow.Flow

interface GetUserAssignment {
    operator fun invoke(
        userId: Long
    ): Flow<GetUserAssignmentState>
}