package id.bluebird.mall.feature_monitoring.main

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.bluebird.mall.core.utils.DialogUtils
import id.bluebird.mall.feature_monitoring.R
import id.bluebird.mall.feature_monitoring.databinding.MonitoringFragmentBinding
import id.bluebird.mall.feature_monitoring.tableview.MonitoringTableAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MonitoringFragment: Fragment() {
    private lateinit var mBinding: MonitoringFragmentBinding
    private lateinit var tableAdapter: MonitoringTableAdapter
    private val monitoringViewModel: MonitoringViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.monitoring_fragment, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = monitoringViewModel
            isNotificationVisible = true
            state = MonitoringState.OnProgressGetList
        }
        setHasOptionsMenu(true)
        initTable()
        monitoringViewModel.init()

        monitoringViewModel.notificationVisibility.observe(viewLifecycleOwner) {
            mBinding.isNotificationVisible = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                monitoringViewModel.monitoringState.collect {
                    mBinding.state = it
                    when(it) {
                        is MonitoringState.OnProgressGetList -> {
                        }
                        is MonitoringState.OnFailedGetList -> {
                            DialogUtils.showSnackbar(view, SpannableStringBuilder("Error when getting data"), R.color.warning_0)
                        }
                        is MonitoringState.OnSuccessGetList -> {
                            tableAdapter.setItem(it.data)
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.rotateScreen -> {
                when(requireActivity().requestedOrientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    else -> {}
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.monitoring_action, menu)
    }

    private fun initTable() {
        tableAdapter = MonitoringTableAdapter()
        mBinding.tableView.apply {
            setAdapter(tableAdapter)
        }
    }

}