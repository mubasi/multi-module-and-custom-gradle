package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.SearchUserState
import kotlinx.coroutines.flow.Flow


interface SearchUser {
    operator fun invoke(param: String?): Flow<SearchUserState>
}