package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.SearchUser
import id.bluebird.mall.domain.user.model.UserSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class SearchUserCases(private val userRepository: UserRepository) : SearchUser {
    override fun invoke(param: String?): Flow<List<UserSearch>> = flow {
        if (param == null) {
            throw NullPointerException()
        }
        val result = userRepository.searchUser(param)
        emitAll(result.transform {
            val resultSearchItems = mutableListOf<UserSearch>()
            it.searchResultList.forEach { userItem ->
                userItem.apply {
                    resultSearchItems.add(
                        UserSearch(
                            id = id,
                            username = username,
                            uuid = uuid,
                            status = UserSearch.convertStatusInfo(this.status)
                        )
                    )
                }
            }
            emit(resultSearchItems)
        })
    }
}