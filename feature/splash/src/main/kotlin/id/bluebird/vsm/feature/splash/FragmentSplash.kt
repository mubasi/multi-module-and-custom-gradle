package id.bluebird.vsm.feature.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import id.bluebird.vsm.feature.splash.databinding.FragmentSplashBinding

class FragmentSplash : Fragment() {

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
            val getToken = Hawk.get("accessToken") ?: ""
            val nav = if (getToken.isNotEmpty()) {
                NavigationSealed.QueueFleet(destination = R.id.splashFragment, frag = this)
            } else {
                NavigationSealed.Login(destination = R.id.splashFragment, frag = this)
            }
            NavigationNav.navigate(nav)
        }, 2000)
    }
}