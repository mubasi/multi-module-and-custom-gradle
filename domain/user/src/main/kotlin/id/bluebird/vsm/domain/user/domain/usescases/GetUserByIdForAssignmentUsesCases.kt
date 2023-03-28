package id.bluebird.vsm.domain.user.domain.usescases

import id.bluebird.vsm.core.extensions.isUserOfficer
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserByIdForAssignment
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.user.model.UserAssignment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.singleOrNull

class GetUserByIdForAssignmentUsesCases(val getUserId: GetUserId) :
    GetUserByIdForAssignment {
    override fun invoke(
        userId: Long,
        locationIdNav: Long?,
        subLocationIdNav: Long?
    ): Flow<GetUserByIdForAssignmentState> = flow {
        val getUserId = getUserId.invoke(userId = userId)
            .singleOrNull() ?: throw NullPointerException()
        when (getUserId) {
            is GetUserByIdState.Success -> {
                getUserId.result.apply {
                    val userAssignment = UserAssignment(
                        id = id,
                        subLocationId = getAssignSubLocation(
                            subLocationId = subLocationsId.first(),
                            roleId = roleId,
                            subLocationNav = subLocationIdNav
                        ),
                        locationId = getAssignLocation(
                            locationId = locationId, roleId = roleId,
                            locationNav = locationIdNav
                        ),
                        isOfficer = roleId.isUserOfficer(),
                        locationName = getUserId.result.locationName,
                        subLocationName = getSubLocationName(
                            roleId = roleId,
                            subLocationName = locationName
                        ),
                        prefix = getUserId.result.prefix
                    )
                    emit(GetUserByIdForAssignmentState.Success(userAssignment))
                }
            }
            GetUserByIdState.UserIsNotFound -> {
                emit(GetUserByIdForAssignmentState.UserNotFound)
            }
        }
    }

    private fun getSubLocationName(roleId: Long, subLocationName: String): String? =
        if (roleId.isUserOfficer()) {
            subLocationName
        } else {
            null
        }

    private fun getAssignSubLocation(
        subLocationId: Long,
        roleId: Long,
        subLocationNav: Long?
    ): Long {
        return if (roleId.isUserOfficer()) {
            subLocationId
        } else {
            subLocationNav ?: subLocationId
        }
    }

    private fun getAssignLocation(locationId: Long, roleId: Long, locationNav: Long?): Long {
        return if (roleId.isUserOfficer()) {
            locationId
        } else {
            locationNav ?: locationId
        }
    }
}