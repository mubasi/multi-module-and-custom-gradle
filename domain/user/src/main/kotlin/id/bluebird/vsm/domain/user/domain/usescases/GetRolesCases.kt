package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.GetRoles
import id.bluebird.vsm.domain.user.model.RoleParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GetRolesCases(private val userRepository: UserRepository) : GetRoles {
    override fun invoke(): Flow<List<RoleParam>> = flow {
        val transform = userRepository.getRoles()
            .flowOn(Dispatchers.IO)
            .transform {
                val list = mutableListOf<RoleParam>()
                it.roleItemsList.forEach { item ->
                    list.add(RoleParam(id = item.id, name = item.rolename))
                }
                emit(list)
            }
        emitAll(transform)
    }
}