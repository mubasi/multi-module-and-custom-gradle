package id.bluebird.mall.domain.user.domain.usescases

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import id.bluebird.mall.core.utils.hawk.AuthUtils.putAccessToken
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
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
                Log.d("LoginCase", "firebase data: ${it.firebaseAuthSettings}")
            }
            auth.addAuthStateListener(authListener)
            userRepository.doLogin(loginParam = param).transform {
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