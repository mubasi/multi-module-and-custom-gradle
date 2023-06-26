package id.bluebird.vsm.pangkalan.extensions

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import id.bluebird.vsm.pangkalan.R

fun Toolbar.setToolbarBackArrow(
    actionBar: ActionBar?
) {

    actionBar?.let {
        it.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_baseline_clear_24
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
                R.drawable.ic_baseline_clear_24
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