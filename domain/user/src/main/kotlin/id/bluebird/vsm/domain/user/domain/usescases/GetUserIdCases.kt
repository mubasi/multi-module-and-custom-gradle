package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.user.model.CreateUserResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GetUserIdCases(private val userRepository: UserRepository) : GetUserId {

    private lateinit var mResult: CreateUserResult

    override fun invoke(userId: Long): Flow<GetUserByIdState> = flow {
        if (userId > 0) {
            val response = userRepository.getUserById(userId)
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            response.run {
                val list = mutableListOf<Long>()
                this.userAssignmentList.forEach {
                    list.add(it.subLocation)
                }
                mResult = CreateUserResult(
                    name = this.name,
                    username = this.username,
                    id = this.userId,
                    roleId = this.userRole,
                    locationId = this.userAssignmentList.first().locationId,
                    locationName = this.userAssignmentList.first().locationName,
                    subLocationsId = list,
                    subLocationName = this.userAssignmentList.first().subLocationName
                )
                emit(GetUserByIdState.Success(mResult))
            }
        } else {
            emit(GetUserByIdState.UserIsNotFound)
        }
    }


}