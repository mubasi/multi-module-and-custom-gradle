package id.bluebird.mall.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import id.bluebird.mall.core.CommonState
import id.bluebird.mall.core.GeneralError
import id.bluebird.mall.core.HomeState
import id.bluebird.mall.core.ui.BaseFragment
import id.bluebird.mall.core.utils.hawk.AuthUtils
import id.bluebird.mall.home.databinding.FragmentHomeBinding
import id.bluebird.mall.home.dialog.Action
import id.bluebird.mall.home.dialog.ActionBottomSheet
import id.bluebird.mall.home.dialog.RitaseDialogFragment
import id.bluebird.mall.home.utils.HomeDialogState
import id.bluebird.mall.navigation.NavigationNav
import id.bluebird.mall.navigation.NavigationSealed
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment() {
    private val mHomeViewModel: HomeViewModel by viewModel()
    private lateinit var mBinding: FragmentHomeBinding
    private var mRitaseDialog: Dialog? = null
    private var mActionBottomSheet: Dialog? = null
    private lateinit var mVp2Home: ViewPager2
    private lateinit var mTabLayout: TabLayout
    private val mSlideFragmentAdapter: SlideFragmentAdapter by lazy {
        SlideFragmentAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mBinding.vm = mHomeViewModel
        mBinding.lifecycleOwner = this
        mVp2Home = mBinding.llMainBodyHome.includeViewPagerHome.vpHome
        mTabLayout = mBinding.llMainBodyHome.includeViewPagerHome.tlHome
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
        mVp2Home.adapter = mSlideFragmentAdapter
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
        mHomeViewModel.homeDialogStateIdle()
        cancelAllDialog()
    }

    private fun homeStateListener() {
        mHomeViewModel.homeDialogState.observe(viewLifecycleOwner) {
            when (it) {
                is HomeDialogState.SuccessCurrentQueue -> {
                    RitaseDialogFragment().show(childFragmentManager, RitaseDialogFragment.TAG)
                }
                is HomeDialogState.SkipCurrentQueue -> {
                    ActionBottomSheet(Action.SKIP, it.item).show(
                        childFragmentManager,
                        ActionBottomSheet.TAG
                    )
                }
                is HomeDialogState.RestoreQueue -> {
                    ActionBottomSheet(Action.RESTORE, it.item).show(
                        childFragmentManager,
                        ActionBottomSheet.TAG
                    )
                }
            }
        }
        mHomeViewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                HomeState.Logout -> {
                    ActionBottomSheet(Action.LOGOUT).show(
                        childFragmentManager,
                        ActionBottomSheet.TAG
                    )
                }
                HomeState.LogoutSuccess -> {
                    AuthUtils.logout()
                    NavigationNav.navigate(NavigationSealed.Login(null, this))
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

                is HomeState.SuccessRitase -> {
                    val number = "${getString(R.string.number_queue_message)} ${it.queueNumber}"
                    val message =
                        "${getString(R.string.ritase)} $number ${getString(R.string.success_note)}"
                    val spannable = SpannableString(message)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        7,
                        23,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    topSnackBarSuccess(spannable)
                }
                is HomeState.SuccessSkiped -> {
                    val message =
                        "${getString(R.string.number_queue_message_caps)} ${it.queueNumber} ${
                            getString(
                                R.string.delay_success
                            )
                        }"
                    val spannable = SpannableString(message)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        16,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    topSnackBarDelay(spannable)
                }
                is HomeState.SuccessRestored -> {
                    val message =
                        "${getString(R.string.number_queue_message_caps)} ${it.queueNumber} ${
                            getString(
                                R.string.restore_success
                            )
                        }"
                    val spannable = SpannableString(message)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        16,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    topSnackBarSuccess(spannable)
                }
                is CommonState.Error -> {
                    cancelAllDialog()
                }
                CommonState.ConnectionNotFound -> {
//                    topSnackBarError(ExceptionHandler.CONNECTION_NOT_FOUND)
                }
                HomeState.ParamSearchQueueEmpty -> {
//                    topSnackBarError(ExceptionHandler.SEARCH_CANNOT_EMPTY)
                }
                HomeState.ParamSearchQueueLessThanTwo -> {
//                    topSnackBarError(ExceptionHandler.SEARCH_CANNOT_LESS_THAN_TWO_CHARACTER)
                }
                HomeState.SearchResultIsEmpty,
                HomeState.ShowSearchResult,
                CommonState.Progress -> {
                    // do nothing
                }
                HomeState.CurrentQueueIsEmpty -> {
//                    topSnackBarError(ExceptionHandler.CURRENT_QUEUE_CANNOT_EMPTY)
                }
                else -> {
                    generalError(it as GeneralError)
                }
            }
        }
    }

    private fun cancelAllDialog() {
        mActionBottomSheet?.dismiss()
        mRitaseDialog?.dismiss()
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
}