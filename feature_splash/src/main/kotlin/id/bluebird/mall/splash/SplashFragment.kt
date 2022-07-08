package id.bluebird.mall.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import id.bluebird.mall.core.ui.BaseFragment
import id.bluebird.mall.core.utils.hawk.AuthUtils
import id.bluebird.mall.navigation.NavigationNav
import id.bluebird.mall.navigation.NavigationSealed
import id.bluebird.mall.splash.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentSplashBinding>(
            inflater,
            R.layout.fragment_splash,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            val nav = if (AuthUtils.getAccessToken().isNotEmpty()) {
                NavigationSealed.QueueFleet(destination = R.id.splashFragment, frag = this)
            } else {
                NavigationSealed.Login(destination = R.id.splashFragment, frag = this)
            }
            NavigationNav.navigate(nav)
        }, 2000)
    }
}