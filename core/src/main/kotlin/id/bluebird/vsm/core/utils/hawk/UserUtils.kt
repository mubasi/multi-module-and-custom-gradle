package id.bluebird.vsm.core.utils.hawk

import com.orhanobut.hawk.Hawk

object UserUtils {
    private const val USER_ROLE = "userRole"
    private const val USERNAME = "username"
    private const val LOCATION_ID = "locationId"
    private const val UUID = "uuid"
    private const val USER_ID = "userId"
    private const val FLEET_TYPE_ID = "fleetTypeId"
    private const val IS_USER_AIRPORT = "isUserAirport"

    const val ADMIN = "ADMIN"
    const val SUPER = "SUPERADMIN"
    const val OFFICER = "PETUGAS"
    const val SVP = "SUPERVISOR"

    private fun String.putUserRole(): Boolean = Hawk.put(USER_ROLE, this)

    private fun String.putUsername(): Boolean = Hawk.put(USERNAME, this)

    private fun String.putUuid(): Boolean = Hawk.put(UUID, this)

    private fun Long.putUserId(): Boolean = Hawk.put(USER_ID, this)

    private fun Long.putLocationId(): Boolean = Hawk.put(LOCATION_ID, this)

    private fun Long.putFleetTypeId(): Boolean = Hawk.put(FLEET_TYPE_ID, this)

    private fun Boolean.putIsUserAirport(): Boolean = Hawk.put(IS_USER_AIRPORT, this)

    fun putUser(
        userId: Long,
        locationId: Long,
        uuid: String,
        userRole: String,
        username: String,
        fleetTypeId: Long,
        isUserAirport: Boolean
    ): Boolean {
        return userId.putUserId() &&
                locationId.putLocationId() &&
                uuid.putUuid() &&
                userRole.putUserRole() &&
                username.putUsername() &&
                fleetTypeId.putFleetTypeId() &&
                isUserAirport.putIsUserAirport()
    }

    fun getLocationId(): Long = Hawk.get(LOCATION_ID) ?: -1

    fun getFleetTypeId(): Long = Hawk.get(FLEET_TYPE_ID) ?: -1

    fun getPrivilege(): String? = Hawk.get(USER_ROLE)

    fun getUserId(): Long = Hawk.get(USER_ID) ?: -1

    fun getUUID(): String = Hawk.get(UUID) ?: ""

    fun getUsername(): String? = Hawk.get(USERNAME)

    fun getIsUserAirport(): Boolean = Hawk.get(IS_USER_AIRPORT)

    fun isUserOfficer() = getPrivilege() == OFFICER
}