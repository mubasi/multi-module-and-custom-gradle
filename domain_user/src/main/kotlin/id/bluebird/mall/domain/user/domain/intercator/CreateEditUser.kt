package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.UserDomainState
import id.bluebird.mall.domain.user.model.CreateUserParam
import kotlinx.coroutines.flow.Flow

interface CreateEditUser {
    operator fun invoke(model: CreateUserParam): Flow<UserDomainState<String>>
}