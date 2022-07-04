package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.model.UserSearch
import kotlinx.coroutines.flow.Flow


interface SearchUser {
    operator fun invoke(param: String?): Flow<List<UserSearch>>
}