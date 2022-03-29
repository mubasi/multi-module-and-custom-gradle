package id.bluebird.mall.officer.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.databinding.FragmentHomeBinding
import id.bluebird.mall.officer.ui.BaseFragment
import id.bluebird.mall.officer.ui.home.dialog.Action
import id.bluebird.mall.officer.ui.home.dialog.ActionBottomSheet
import id.bluebird.mall.officer.ui.home.dialog.RitaseDialogFragment
import id.bluebird.mall.officer.utils.AuthUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : BaseFragment() {
    private val mHomeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var mBinding: FragmentHomeBinding
    private var mRitaseDialog: Dialog? = null
    private var mActionBottomSheet: Dialog? = null
    private lateinit var mVp2Home: ViewPager2
    private lateinit var mTabLayout: TabLayout
    private val mSlideFragment: SlideFragment by lazy {
        SlideFragment(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mBinding.vm = mHomeViewModel
        mBinding.lifecycleOwner = this
        mVp2Home = mBinding.includeViewPagerHome.vpHome
        mTabLayout = mBinding.includeViewPagerHome.tlHome
        return mBinding.root
    }

    private fun setTabLayout() {
        TabLayoutMediator(mTabLayout, mVp2Home) { tab, position ->
            tab.text = if (position == 0) {
                "${getString(R.string.waiting)} (0)"
            } else {
                "${getString(R.string.delay)} (0)"
            }
        }.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateMainBody(mBinding.clMainBodyHome)
        homeStateListener()
        touchListener()
        mVp2Home.adapter = mSlideFragment
        setTabLayout()
        mHomeViewModel.let { vm ->
            vm.queueWaiting.observe(viewLifecycleOwner) {
                mTabLayout.getTabAt(0)?.text = "${getString(R.string.waiting)} (${it.size})"
            }
            vm.queueDelay.observe(viewLifecycleOwner) {
                mTabLayout.getTabAt(1)?.text = "${getString(R.string.delay)} (${it.size})"
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchListener() {
        mBinding.coordinatorRootHome.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    v?.let {
                        if ((v is EditText).not()) {
                            v.clearFocus()
                            val imm = context?.getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            ) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        } else {
                            v.isFocusable = true
                        }
                        return true
                    }
                    return false
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHomeViewModel.homeStateOnIdle()
        cancelAllDialog()
    }

    private fun homeStateListener() {
        mHomeViewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                HomeState.Logout -> {
                    clearSearchFocus()
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
                is HomeState.SuccessCurrentQueue -> {
                    RitaseDialogFragment().show(childFragmentManager, RitaseDialogFragment.TAG)
                }
                is HomeState.SkipCurrentQueue -> {
                    ActionBottomSheet(Action.SKIP, it.item).show(
                        childFragmentManager,
                        Action.SKIP.name
                    )
                }
                is HomeState.SuccessRitase -> {
                    topSnackBar("${it.queueNumber} selesai", null, null)
                }
                is HomeState.SuccessSkiped -> {
                    topSnackBar("${it.queueNumber} dilewatkan", null, null)
                }
                is CommonState.Error -> {
                    cancelAllDialog()
                }
                HomeState.ParamSearchQueueEmpty -> {
                    topSnackBarError(getString(R.string.search_cannot_empty))
                }
                HomeState.ParamSearchQueueLessThanTwo -> {
                    topSnackBarError(getString(R.string.search_cannot_less_than_two))
                }
            }
        }
    }

    private fun cancelAllDialog() {
        mActionBottomSheet?.dismiss()
        mRitaseDialog?.dismiss()
    }

    private fun clearSearchFocus() {
        if (mBinding.edtSearchQueueMain.hasFocus()) {
            mBinding.edtSearchQueueMain.clearFocus()
            val imm = context?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as? InputMethodManager
            imm?.hideSoftInputFromWindow(mBinding.coordinatorRootHome.windowToken, 0)
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