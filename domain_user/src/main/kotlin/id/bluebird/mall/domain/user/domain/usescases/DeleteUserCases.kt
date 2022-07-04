package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.DeleteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class DeleteUserCases(private val userRepository: UserRepository) : DeleteUser {
    override fun invoke(uuid: String): Flow<Boolean> = flow {
        val response = userRepository.deleteUser(uuid, UserUtils.getUserId())
        emitAll(response.transform {
            emit(true)
        })
    }
}