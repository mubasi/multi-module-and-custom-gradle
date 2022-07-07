package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.model.RoleParam
import kotlinx.coroutines.flow.Flow

interface GetRoles {
    operator fun invoke(): Flow<List<RoleParam>>
}