package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.model.UserSearchParam
import kotlinx.coroutines.flow.Flow


interface SearchUser {
    operator fun invoke(param: String?): Flow<List<UserSearchParam>>
}