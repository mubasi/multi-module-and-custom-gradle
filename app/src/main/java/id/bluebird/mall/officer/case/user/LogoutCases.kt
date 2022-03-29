package id.bluebird.mall.officer.case.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LogoutCases {
    operator fun invoke(): Flow<Boolean>
}

class LogoutCasesImpl : LogoutCases {
    override fun invoke(): Flow<Boolean> = flow {
        kotlinx.coroutines.delay(1000)
        emit(true)
    }
}