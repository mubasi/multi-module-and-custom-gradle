package id.bluebird.mall.domain.user.domain.intercator

import kotlinx.coroutines.flow.Flow

interface ForceLogout {
    operator fun invoke(uuid: String): Flow<Boolean>
}