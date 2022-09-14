package id.bluebird.vsm.feature.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import id.bluebird.vsm.core.BuildConfig
import id.bluebird.vsm.core.ui.BaseFragment
import id.bluebird.vsm.feature.login.databinding.FragmentLoginBinding
import id.bluebird.vsm.feature.login.dialog.DialogError
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment() {

    private val mLoginViewModel: LoginViewModel by viewModel()
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.vm = mLoginViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        mBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateMainBody(mBinding.clMainBodyLogin)
        mBinding.tvVersionNameLogin.text = BuildConfig.VERSION_NAME
        onBackPressedFragment()
        state()
    }

    private fun onBackPressedFragment() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        })
    }

    private fun state() {
        mLoginViewModel.loginState.observe(viewLifecycleOwner) {
            when (it) {
                is LoginState.Error -> {
                    DialogError(it.errorType).show(
                        childFragmentManager,
                        "aa"
                    )
                }
                is LoginState.Phone -> {
                    intentToDial()
                }
                LoginState.Success -> {
                    requireActivity().recreate()
                    NavigationNav.navigate(
                        NavigationSealed.QueueFleet(
                            destination = R.id.loginFragment,
                            frag = this
                        )
                    )
                }
                LoginState.PasswordIsEmpty -> {
                    topSnackBarError(getString(R.string.password_cannot_empty))
                }
                LoginState.UsernameIsEmpty -> {
                    topSnackBarError(getString(R.string.username_cannot_empty))
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun intentToDial() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:08120812")
        context?.startActivity(intent)
    }
}