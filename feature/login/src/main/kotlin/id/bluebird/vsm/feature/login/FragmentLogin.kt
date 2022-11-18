package id.bluebird.vsm.feature.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.feature.login.databinding.FragmentLoginBinding
import id.bluebird.vsm.feature.login.dialog.DialogError
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogin : Fragment() {

    private val mLoginViewModel: LoginViewModel by viewModel()
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        mBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.vm = mLoginViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
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
                    showSnackbar(Html.fromHtml(getString(R.string.password_cannot_empty),1), R.color.danger_1)
                }
                LoginState.UsernameIsEmpty -> {
                    showSnackbar(Html.fromHtml(getString(R.string.username_cannot_empty),1), R.color.danger_1)
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


    fun showSnackbar(message: Spanned, color: Int){
        val snackbar = Snackbar.make(requireActivity().window.decorView.rootView,message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(-10,50,-10,0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), color))
        snackbar.show()
    }
}