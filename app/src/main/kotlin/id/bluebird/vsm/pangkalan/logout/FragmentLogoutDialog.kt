package id.bluebird.vsm.pangkalan.logout

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.core.utils.hawk.AuthUtils
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import id.bluebird.vsm.pangkalan.R
import id.bluebird.vsm.pangkalan.databinding.BottomSheetFregmantBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentLogoutDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "requestLogout"
    }

    private lateinit var binding: BottomSheetFregmantBinding
    private val _logoutDialogViewModel: LogoutDialogViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_fregmant, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.logoutViewModel = _logoutDialogViewModel

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _logoutDialogViewModel.logoutDialogState.collect{
                    when(it) {
                        is LogoutDialogState.Err -> {
                            Toast.makeText(requireContext(), it.err.message, Toast.LENGTH_SHORT).show()
                        }
                        is LogoutDialogState.MessageError -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        LogoutDialogState.CancelDialog -> {
                            dialog?.dismiss()
                        }
                        LogoutDialogState.ProsesDialog -> {
                            dialog?.dismiss()
                            LocationNavigationTemporary.removeTempData()
                            AuthUtils.logout()
                            NavigationNav.navigate(
                                NavigationSealed.Login(
                                    destination = null,
                                    frag = this@FragmentLogoutDialog
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}