package id.bluebird.mall.domain.user.domain.intercator

import kotlinx.coroutines.flow.Flow

interface DeleteUser {
    operator fun invoke(uuid: String): Flow<Boolean>
}