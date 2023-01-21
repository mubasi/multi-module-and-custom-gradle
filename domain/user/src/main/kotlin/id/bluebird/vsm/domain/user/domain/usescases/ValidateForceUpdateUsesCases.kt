package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.ValidateForceUpdateState
import id.bluebird.vsm.domain.user.domain.intercator.ValidateForceUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import proto.UserOuterClass

class ValidateForceUpdateUsesCases(private val userRepository: UserRepository) :
    ValidateForceUpdate {

    override fun invoke(key: String, codeVersion: Long?): Flow<ValidateForceUpdateState> = flow {
        if (codeVersion == null) {
            emit(ValidateForceUpdateState.CodeVersionNotFound)
            return@flow
        }
        val result = userRepository.getSplashConfig(key)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        validatePlayStoreUrl(url = result.playStoreUrl, flowCollector = this)
        validateCodeVersion(
            splashConfig = result,
            codeVersion = codeVersion,
            flowCollector = this
        )
    }

    private suspend fun validateCodeVersion(
        splashConfig: UserOuterClass.SplashConfigResponse,
        codeVersion: Long,
        flowCollector: FlowCollector<ValidateForceUpdateState>
    ) {
        flowCollector.apply {
            if (codeVersion < splashConfig.versionCode) {
                emit(
                    ValidateForceUpdateState.FoundNewVersion(
                        url = splashConfig.playStoreUrl,
                        versionName = splashConfig.versionName
                    )
                )
            } else {
                emit(ValidateForceUpdateState.NotFoundNewVersion)
            }
        }
    }

    private suspend fun validatePlayStoreUrl(
        url: String?,
        flowCollector: FlowCollector<ValidateForceUpdateState>
    ) {
        if (url.isNullOrEmpty()) {
            flowCollector.emit(ValidateForceUpdateState.NotFoundNewVersion)
        }
    }
}