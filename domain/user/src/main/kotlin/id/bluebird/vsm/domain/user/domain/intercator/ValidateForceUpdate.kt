package id.bluebird.vsm.domain.user.domain.intercator

import id.bluebird.vsm.domain.user.ValidateForceUpdateState
import kotlinx.coroutines.flow.Flow

interface ValidateForceUpdate {

    fun invoke(key: String, codeVersion: Long?): Flow<ValidateForceUpdateState>
}