package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.UserDomainState
import id.bluebird.mall.domain.user.model.CreateUserResult
import kotlinx.coroutines.flow.Flow

interface GetUserById {
    operator fun invoke(userId: Long): Flow<UserDomainState<CreateUserResult>>
}