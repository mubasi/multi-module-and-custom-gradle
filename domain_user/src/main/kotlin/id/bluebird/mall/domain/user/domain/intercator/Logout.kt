package id.bluebird.mall.domain.user.domain.intercator

import kotlinx.coroutines.flow.Flow

interface Logout {
    operator fun invoke(id: Long): Flow<Long>
}