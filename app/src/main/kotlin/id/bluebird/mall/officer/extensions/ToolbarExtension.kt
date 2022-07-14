package id.bluebird.mall.officer.extensions

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import id.bluebird.mall.feature_user_management.R

fun Toolbar.setToolbarAddFleetFragment(
    actionBar: ActionBar?
) {

    actionBar?.let {
        it.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_arrow_back
            )
        )
        it.setDisplayShowHomeEnabled(true)
    }

}

fun Toolbar.setToolbarCreateUserFragment(
    actionBar: ActionBar?,
    args: Bundle?
) {
    actionBar?.let {
        it.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_arrow_back
            )
        )
        it.setDisplayShowHomeEnabled(true)
        val userId = args?.getLong("userId") ?: -1
        if (userId < 1) {
            it.title = "Tambah pengguna"
        } else {
            it.title = "Ubah pengguna"
        }
    }
}

fun Toolbar.backArrowButton(navController: NavController, destinationId: Int) {
    setNavigationOnClickListener {
        navController.popBackStack(
            destinationId = destinationId,
            inclusive = true,
            saveState = false
        )
    }
}