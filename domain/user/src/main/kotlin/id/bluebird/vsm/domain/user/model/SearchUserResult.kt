package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep

@Keep
data class SearchUserResult(
    val searchResult: ArrayList<UserSearchParam>
)