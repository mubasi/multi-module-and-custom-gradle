package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserDomainState
import id.bluebird.mall.domain.user.UserErr
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.CreateEditUser
import id.bluebird.mall.domain.user.model.CreateUserParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class CreateEditUserCases(private val userRepository: UserRepository) : CreateEditUser {

    private lateinit var mFlowCollector: FlowCollector<UserDomainState<String>>

    override fun invoke(model: CreateUserParam): Flow<UserDomainState<String>> = flow {
        mFlowCollector = this
        validate(model)
        if (model.id > 0) {
            editUser(model)
        } else {
            val result = userRepository.createUser(
                model.copy(
                    id = 0,
                    locationId = UserUtils.getLocationId()
                )
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            emit(UserDomainState.Success(result.username))
        }
    }

    private suspend fun editUser(
        model: CreateUserParam
    ) {
        if (model.newPassword.isNullOrEmpty()) {
            mFlowCollector.emit(UserErr.NewPasswordIsEmpty)
        }
        userRepository.editUser(model)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        mFlowCollector.emit(UserDomainState.Success(model.username ?: ""))
    }

    private suspend fun validate(model: CreateUserParam) {
        model.run {
            if (roleId < 1) {
                mFlowCollector.emit(UserErr.RoleIsNotSelected)
            }
            if (password.isEmpty()) {
                mFlowCollector.emit(UserErr.PasswordIsEmpty)
            }
            if (username.isNullOrEmpty()) {
                mFlowCollector.emit(UserErr.UsernameIsEmpty)
            }
            if (name.isNullOrEmpty()) {
                mFlowCollector.emit(UserErr.NameIsEmpty)
            }
        }
    }
}