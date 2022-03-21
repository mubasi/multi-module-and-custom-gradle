package id.bluebird.mall.officer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.databinding.FragmentHomeBinding
import id.bluebird.mall.officer.ui.BaseFragment
import id.bluebird.mall.officer.ui.MainViewModel
import id.bluebird.mall.officer.utils.AuthUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mBinding.vm = mHomeViewModel
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateMainBody(mBinding.clMainBodyHome)
        homeStateListener()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.mqttConnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.mqttDisconnect()
    }

    private fun homeStateListener() {
        mHomeViewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                HomeState.Logout -> {
                    dialogLogout()
                }
                HomeState.OnSync -> {
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                CommonState.Idle -> {
                    requireActivity().window.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                HomeState.DummyIndicator -> {
                    dialogIndicatorSample()
                }
                is CommonState.Error -> TODO()
                HomeState.ParamSearchQueueEmpty -> {
                    topSnackBarError(getString(R.string.search_cannot_empty))
                }
                HomeState.ParamSearchQueueLessThanTwo -> {
                    topSnackBarError(getString(R.string.search_cannot_less_than_two))
                }
            }
        }
    }

    private fun dialogIndicatorSample() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(
                "Green"
            ) { dialog, _ ->
                mHomeViewModel.changeIndicator(true)
                dialog.cancel()
            }
            .setNegativeButton("Red") { dialog, _ ->
                mHomeViewModel.changeIndicator(false)
                dialog.cancel()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun dialogLogout() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setPositiveButton(
                R.string.exit
            ) { dialog, _ ->
                AuthUtils.logout()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                dialog.cancel()
            }
            .setTitle(R.string.exit)
            .setMessage(R.string.exit_message)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .setCancelable(false)
            .create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }
}