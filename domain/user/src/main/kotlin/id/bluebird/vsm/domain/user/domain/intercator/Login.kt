package id.bluebird.vsm.domain.user.domain.intercator

import kotlinx.coroutines.flow.Flow

interface Login {
    operator fun invoke(username: String?, password: String?): Flow<Boolean>
}