package id.bluebird.vsm.feature.user_management.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ModifyUserAction: Parcelable {
    @Parcelize
    object Create: ModifyUserAction()

    @Parcelize
    object Edit: ModifyUserAction()

    @Parcelize
    object Delete: ModifyUserAction()

    @Parcelize
    object ForceLogout: ModifyUserAction()

    @Parcelize
    object Nothing: ModifyUserAction()
}