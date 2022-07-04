package id.bluebird.mall.domain.user.model

data class UserSearch(val id: Long, val username: String, val uuid: String, val status: Boolean) {
    companion object {
        fun convertStatusInfo(info: String): Boolean = info == "Active"
    }
}