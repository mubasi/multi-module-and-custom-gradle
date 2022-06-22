package id.bluebird.mall.domain.user

import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl : UserRepository {
    override fun doLogin(loginParam: LoginParam): Flow<String> = flow {}

    override fun doLogout(id: Long) {
        TODO("Not yet implemented")
    }
}