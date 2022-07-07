package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.SearchUser
import id.bluebird.mall.domain.user.model.UserSearchParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class SearchUserCases(private val userRepository: UserRepository) : SearchUser {
    override fun invoke(param: String?): Flow<List<UserSearchParam>> = flow {
        if (param == null) {
            throw NullPointerException()
        }
        val result = userRepository.searchUser(param)
        emitAll(result.transform {
            val resultSearchItems = mutableListOf<UserSearchParam>()
            it.searchResultList.forEach { userItem ->
                userItem.apply {
                    resultSearchItems.add(
                        UserSearchParam(
                            id = id,
                            username = username,
                            uuid = uuid,
                            status = UserSearchParam.convertStatusInfo(this.status)
                        )
                    )
                }
            }
            emit(resultSearchItems)
        })
    }
}