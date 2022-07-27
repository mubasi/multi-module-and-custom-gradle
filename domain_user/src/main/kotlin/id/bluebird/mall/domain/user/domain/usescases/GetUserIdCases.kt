package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.GetUserId
import id.bluebird.mall.domain.user.model.CreateUserResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetUserIdCases(private val userRepository: UserRepository) : GetUserId {

    private lateinit var mResult: CreateUserResult

    override fun invoke(userId: Long?): Flow<GetUserByIdState> = flow {
        val response = userRepository.getUserById(userId ?: UserUtils.getUserId())
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
    }
}