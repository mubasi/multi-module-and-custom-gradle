package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import kotlinx.coroutines.flow.Flow

interface GetUserByIdForAssignment {
    fun invoke(userId:Long, locationIdNav:Long?, subLocationIdNav:Long?):Flow<GetUserByIdForAssignmentState>
}