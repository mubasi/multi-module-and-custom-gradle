package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.GetUserByIdState
import kotlinx.coroutines.flow.Flow

interface GetUserId {
    operator fun invoke(userId: Long): Flow<GetUserByIdState>
}