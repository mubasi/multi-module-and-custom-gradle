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
import id.bluebird.mall.officer.utils.AuthUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by viewModel()

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
        homeStateListener()
    }

    private fun homeStateListener() {
        with(mHomeViewModel) {
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
                }
            }
        }
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