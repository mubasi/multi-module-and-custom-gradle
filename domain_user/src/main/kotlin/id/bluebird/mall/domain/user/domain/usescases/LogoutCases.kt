package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.ForceLogout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class LogoutCasesImpl(private val userRepository: UserRepository) : ForceLogout {

    override fun invoke(uuid: String): Flow<Boolean> = flow {
        val response = userRepository.forceLogout(uuid)
        emitAll(response.transform {
            emit(true)
        })
    }
}