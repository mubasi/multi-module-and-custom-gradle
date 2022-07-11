package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.GetUserById
import id.bluebird.mall.domain.user.model.CreateUserResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetUserByIdCases(private val userRepository: UserRepository) : GetUserById {

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
                    subLocationsId = list
                )
            }
            emit(GetUserByIdState.Success(mResult))
        } else {
            emit(GetUserByIdState.UserIdIsWrong)
        }
    }
}