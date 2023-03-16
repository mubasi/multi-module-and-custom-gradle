package id.bluebird.vsm.domain.user

import com.google.firebase.auth.FirebaseAuth
import com.google.protobuf.Empty
import id.bluebird.vsm.core.utils.DateUtils.getDateRfc399
import id.bluebird.vsm.core.utils.OkHttpChannel
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.model.CreateUserParam
import id.bluebird.vsm.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.UserGrpc
import proto.UserOuterClass

class UserRepositoryImpl(
    private val userGrpc: UserGrpc.UserBlockingStub = UserGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : UserRepository {

    companion object {
        private const val DEFAULT_VALUE = "0"
        private const val EMAIL_CONST = "@bluebird.group.com"
    }

    override fun doLogin(loginParam: LoginParam): Flow<UserOuterClass.UserLoginResponse> = flow {
        val request = UserOuterClass.UserLoginRequest.newBuilder().apply {
            username = loginParam.username
            password = loginParam.password
            fleetType = loginParam.fleetType
        }
            .build()
        val response = userGrpc.userLogin(request)
        emit(response)
    }

    override fun forceLogout(uuid: String): Flow<UserOuterClass.ForceLogoutResponse> = flow {
        val request = UserOuterClass.ForceLogoutRequest.newBuilder().apply {
            this.uuid = uuid
        }.build()
        val response = userGrpc.forceLogout(request)
        FirebaseAuth.getInstance().signOut()
        emit(response)
    }

    override fun deleteUser(uuid: String, by: Long): Flow<UserOuterClass.DeleteUserResponse> =
        flow {
            val request = UserOuterClass.DeleteUserRequest.newBuilder()
                .apply {
                    this.uuid = uuid
                    deletedBy = by
                }.build()
            val response = userGrpc.deleteUser(request)
            emit(response)
        }

    override fun searchUser(param: String?): Flow<UserOuterClass.SearchUserResponse> = flow {
        val request = UserOuterClass.SearchUserRequest.newBuilder()
            .setUsername(param)
            .build()
        val response = userGrpc.searchUser(request)
        emit(response)
    }

    override fun getRoles(): Flow<UserOuterClass.GetRolesResponse> = flow {
        val result = userGrpc.getUserRoles(Empty.getDefaultInstance())
        emit(result)
    }

    override fun createUser(model: CreateUserParam): Flow<UserOuterClass.CreateUserResponse> =
        flow {
            val request = UserOuterClass.CreateUserRequest.newBuilder()
                .apply {
                    id = model.id
                    userRole = (model.roleId)
                    password = (model.newPassword)
                    username = (model.username)
                    name = (model.name)
                    deletedBy = (DEFAULT_VALUE)
                    fleetType = (UserUtils.getFleetTypeId())
                    email = "${model.username}$EMAIL_CONST"
                    isAirport = (UserUtils.getIsUserAirport())
                    createdAt = getDateRfc399()
                    modifiedAt = getDateRfc399()
                    createdBy = (UserUtils.getUserId().toString())
                    modifiedBy = (DEFAULT_VALUE)
                }
            model.subLocationsId.forEach {
                request.addUserAssignment(
                    UserOuterClass.userAssignmentItem.newBuilder()
                        .apply {
                            idUser = (UserUtils.getUserId())
                            this.locationId = model.locationId
                            this.subLocation = it
                        }
                        .build()
                )
            }
            val result = userGrpc.createUser(request.build())
            emit(result)
        }

    override fun editUser(model: CreateUserParam): Flow<UserOuterClass.EditUserResponse> = flow {
        val currentUserId = UserUtils.getUserId()
        val request = UserOuterClass.EditUserRequest.newBuilder()
            .apply {
                roleId = model.roleId
                username = model.username
                oldPassword = model.password
                newPassword = model.newPassword
                userId = model.id
                email = "${model.username}$EMAIL_CONST"
                modifiedBy = currentUserId
                name = model.name
            }
        model.subLocationsId.forEach {
            request.addUserAssignment(
                UserOuterClass.userAssignmentItem.newBuilder()
                    .apply {
                        idUser = (UserUtils.getUserId())
                        this.locationId = model.locationId
                        this.subLocation = it
                    }
                    .build()
            )
        }
        val result = userGrpc.editUser(request.build())
        emit(result)
    }

    override fun getUserById(id: Long): Flow<UserOuterClass.GetUserByIdResponse> = flow {
        val request = UserOuterClass.GetUserByIdRequest.newBuilder().setUserId(id).build()
        val result = userGrpc.getUserById(request)
        emit(result)
    }

    override fun getUserLocationAssign(
        subLocationsId: List<Long>,
        locationId: Long
    ): List<UserOuterClass.userAssignmentItem> {
        val result = mutableListOf<UserOuterClass.userAssignmentItem>()
        subLocationsId.forEach {
            val userAssignment = UserOuterClass.userAssignmentItem.newBuilder()
                .apply {
                    idUser = (UserUtils.getUserId())
                    this.locationId = (locationId)
                    this.subLocation = (it)
                }
                .build()
            result.add(userAssignment)
        }
        return result
    }

    override fun getSplashConfig(key: String): Flow<UserOuterClass.SplashConfigResponse> = flow {
        val request = UserOuterClass.SplashConfigRequest.newBuilder()
            .apply {
                this.key = key
            }.build()
        emit(userGrpc.getSplashConfig(request))
    }
}