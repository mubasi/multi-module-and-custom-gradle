package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.model.CreateUserParam
import kotlinx.coroutines.flow.Flow

interface CreateEditUser {
    operator fun invoke(model: CreateUserParam): Flow<UserDomainState<String>>
}