package id.bluebird.mall.officer.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import id.bluebird.mall.officer.BuildConfig
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.LoginState
import id.bluebird.mall.officer.databinding.FragmentLoginBinding
import id.bluebird.mall.officer.ui.BaseFragment
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
        binding.lifecycleOwner = this
        mBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvVersionNameLogin.text = BuildConfig.VERSION_NAME
        mLoginViewModel.loginState.observe(this, {
            when (it) {
                is CommonState.Error -> {
                    topSnackBarError(mBinding.clMainBodyLogin, it.error.message ?: "Kesalahan")
                }
                is LoginState.Phone -> {
                    intentToDial()
                }
            }
        })
    }

    fun intentToDial() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:08120812")
        context?.startActivity(intent)
    }


}