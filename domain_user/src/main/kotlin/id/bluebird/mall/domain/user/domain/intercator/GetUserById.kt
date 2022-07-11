package id.bluebird.mall.domain.user.domain.intercator

import id.bluebird.mall.domain.user.GetUserByIdState
import kotlinx.coroutines.flow.Flow

interface GetUserById {
    operator fun invoke(userId: Long): Flow<GetUserByIdState>
}