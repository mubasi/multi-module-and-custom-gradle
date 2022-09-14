package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.model.RoleParam
import kotlinx.coroutines.flow.Flow

interface GetRoles {
    operator fun invoke(): Flow<List<RoleParam>>
}