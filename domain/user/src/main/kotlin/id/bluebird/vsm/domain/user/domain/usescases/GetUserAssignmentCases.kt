package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.domain.user.model.AssignmentLocationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetUserAssignmentCases(
    private val userRepository: UserRepository
) : GetUserAssignment {
    override fun invoke(userId: Long): Flow<GetUserAssignmentState> = flow {
        if (userId > 0) {
            val response = userRepository.getUserAssignment(userId)
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            response.run {
                val it = this.subLocationItemsList[0]
                val item = AssignmentLocationItem(
                    subLocationId = it.subLocationId,
                    subLocationName = it.subLocationName,
                    isDeposition = it.isDeposition,
                    locationId = it.locationId,
                    isWings = it.isWings,
                    prefix = it.prefix,
                    locationName = it.locationName
                )
                emit(GetUserAssignmentState.Success(item))
            }
        } else {
            emit(GetUserAssignmentState.UserNotFound)
        }
    }
}