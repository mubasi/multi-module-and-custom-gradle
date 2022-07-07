package id.bluebird.mall.feature_user_management.utils

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.create.CreateUserFragment
import id.bluebird.mall.feature_user_management.list.UserListFragmentDirections

object NavControllerUserDomain {
    fun setToolbar(
        actionBar: ActionBar?,
        toolbar: Toolbar,
        navigationController: NavController,
        args: Bundle?
    ) {
        actionBar?.let {
            it.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    toolbar.context,
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
            toolbar.setNavigationOnClickListener {
                popBack(navigationController)
            }
        }
    }

    internal fun popBack(navController: NavController) {
        navController.popBackStack(
            destinationId = R.id.createUserFragment,
            inclusive = true,
            saveState = false
        )
    }

    internal fun navigateToCreateFragment(
        fragment: Fragment,
        id: Long?,
        callback: (result: Boolean, bundle: Bundle?) -> Unit
    ) {
        val destination = UserListFragmentDirections.actionUserListFragmentToCreateUserFragment()
        id?.let {
            destination.userId = id
        }
        fragment.apply {
            findNavController().navigate(destination)
            callback(false, null)
            setFragmentResultListener(CreateUserFragment.REQUEST_KEY) { _, bundle ->
                callback(true, bundle)
            }
        }
    }
}