package id.bluebird.vsm.domain.user.domain.usescases

import com.google.firebase.auth.FirebaseAuth
import id.bluebird.vsm.core.utils.hawk.AuthUtils.putAccessToken
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.Login
import id.bluebird.vsm.domain.user.model.LoginParam
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class LoginCaseImpl(private val userRepository: UserRepository) : Login {
    companion object {
        const val USERNAME_EMPTY = "usernameIsEmpty"
        const val PASSWORD_EMPTY = "passwordIsEmpty"
        const val ERROR_TAG = "LoginError"
    }

    override fun invoke(username: String?, password: String?): Flow<Boolean> =
        callbackFlow {
            val param = LoginParam(username = username ?: "", password = password ?: "")
            if (username.isNullOrEmpty()) {
                throw NullPointerException(USERNAME_EMPTY)
            }
            if (password.isNullOrEmpty()) {
                throw NullPointerException(PASSWORD_EMPTY)
            }
            val auth = FirebaseAuth.getInstance()
            val authListener = FirebaseAuth.AuthStateListener {
                trySend(it.currentUser != null)
            }
            auth.addAuthStateListener(authListener)
            userRepository.doLogin(loginParam = param).
            transform {
                val result = it.accessToken.putAccessToken()
                UserUtils.putUser(
                    userId = it.userId,
                    locationId = it.locationId,
                    uuid = it.uuid,
                    userRole = it.userRole,
                    username = username,
                    fleetTypeId = it.fleetType,
                    isUserAirport = it.isAirport
                )

                emit(result to it.firebaseToken)
            }.collect { (result, firebaseToken) ->
                if (result) {
                    auth.signInWithCustomToken(firebaseToken)
                } else {
                    trySend(result)
                }
            }

            awaitClose {
                auth.removeAuthStateListener(authListener)
            }
        }
}