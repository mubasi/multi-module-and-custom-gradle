package id.bluebird.mall.domain.user

import id.bluebird.mall.core.utils.OkHttpChannel
import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.domain.usescases.LoginCaseImpl
import id.bluebird.mall.user.domain.LogoutCases
import id.bluebird.mall.user.domain.LogoutCasesImpl
import org.koin.dsl.module
import proto.UserGrpc

object DomainUserModule {

    val domainUserModule = module {
        includes(userCases, repository, userGrpc)
    }

    private val userCases = module {
        single<LogoutCases> { LogoutCasesImpl() }
        single<Login> { LoginCaseImpl(get()) }
    }

    private val userGrpc = module {
        factory { UserGrpc.newBlockingStub(OkHttpChannel.channel) }
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

}