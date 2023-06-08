package id.bluebird.vsm.feature.splash

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.bluebird.vsm.feature.splash.databinding.FragmentSplashBinding
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSplash : Fragment() {

    private val vm: SplashViewModel by viewModel()

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.splashState.collectLatest {
                    when (it) {
                        SplashState.LoginAsOutletUser -> {
                            NavigationNav.navigate(
                                NavigationSealed.QueueCarFleet (
                                    destination = R.id.splashFragment,
                                    frag = this@FragmentSplash
                                )
                            )
                        }
                        SplashState.LoginAsAirportUser -> {
                            NavigationNav.navigate(
                                NavigationSealed.FleetAirport(
                                    destination = R.id.splashFragment,
                                    frag = this@FragmentSplash
                                )
                            )
                        }
                        SplashState.Login -> {
                            NavigationNav.navigate(
                                NavigationSealed.Login(
                                    destination = R.id.splashFragment,
                                    frag = this@FragmentSplash
                                )
                            )
                        }
                        is SplashState.DoUpdateVersion -> {
                            forceUpdate(url = it.url, versionName = it.versionName)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getVersion()
    }

    private fun getVersion() {
        try {
            val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
           vm.checkNewVersion(codeVersion = getVersionCode(pInfo))
        } catch (e: Exception) {
            vm.checkNewVersion()
        }
    }

    private fun getVersionCode(pInfo:PackageInfo) :Long{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            pInfo.versionCode.toLong()
        }
    }

    private fun forceUpdate(url: String, versionName: String) {
        val dialog = AlertDialog.Builder(this@FragmentSplash.context)
        dialog.setTitle("Versi Baru")
            .setCancelable(false)
            .setMessage("Versi $versionName sudah ada di playstore, harap lakukan pembaharuan versi.")
            .setPositiveButton(
                "Pembaharuan"
            ) { p0, _ ->
                val intent = try {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=id.bluebird.vsm.pangkalan")
                    )
                } catch (e: Exception) {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                }
                startActivity(intent)
                p0.cancel()
            }
        dialog.show()
    }
}