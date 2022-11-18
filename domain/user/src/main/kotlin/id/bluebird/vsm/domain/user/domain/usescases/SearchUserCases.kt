package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.domain.user.SearchUserState
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.SearchUser
import id.bluebird.vsm.domain.user.model.SearchUserResult
import id.bluebird.vsm.domain.user.model.UserSearchParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class SearchUserCases(private val userRepository: UserRepository) : SearchUser {
    override fun invoke(param: String?): Flow<SearchUserState> = flow {
        val result =  userRepository.searchUser(param)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val resultSearchItems = ArrayList<UserSearchParam>()
        result.searchResultList.forEach {
            resultSearchItems.add(
                UserSearchParam(
                    id = it.id,
                    username = it.username,
                    uuid = it.uuid,
                    status = UserSearchParam.convertStatusInfo(it.status)
                )
            )
        }
        emit(SearchUserState.Success(
            searchUserResult = SearchUserResult(resultSearchItems)
        ))
    }
}