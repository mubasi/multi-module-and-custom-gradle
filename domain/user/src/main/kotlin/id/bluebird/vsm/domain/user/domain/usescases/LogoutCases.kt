package id.bluebird.vsm.domain.user.domain.usescases

import com.google.firebase.auth.FirebaseAuth
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.domain.intercator.ForceLogout
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException

class LogoutCasesImpl(private val userRepository: UserRepository) : ForceLogout {

    override fun invoke(uuid: String): Flow<Boolean> = flow {
        userRepository.forceLogout(uuid).singleOrNull() ?: throw NullPointerException()
        try {
            FirebaseAuth.getInstance().signOut()
            emit(true)
        } catch (e: Exception) {
            emit(true)
        }
    }
}