package id.bluebird.vsm.feature.monitoring.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.core.utils.StringUtils
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.MonitoringFragmentBinding
import id.bluebird.vsm.feature.monitoring.edit_buffer.FragmentEditBufferDialog
import id.bluebird.vsm.feature.monitoring.tableview.AdapterMonitoringTable
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentMonitoring: Fragment() {
    private lateinit var mBinding: MonitoringFragmentBinding
    private lateinit var tableAdapter: AdapterMonitoringTable
    private val monitoringViewModel: MonitoringViewModel by viewModel()

    companion object {
        const val ERROR_MESSAGE = "Error when getting data"
    }

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
                        is MonitoringState.OnFailedGetList -> {
                            showNotification(SpannableStringBuilder(ERROR_MESSAGE), R.color.warning_0)
                        }
                        is MonitoringState.OnSuccessGetList -> {
                            tableAdapter.setItem(it.data)
                        }
                        is MonitoringState.RequestEditBuffer -> {
                            it.item?.let { model ->
                                FragmentEditBufferDialog(
                                    model,
                                    monitoringViewModel::onDialogSaveResult
                                )
                                    .show(childFragmentManager, FragmentEditBufferDialog.TAG)
                            }
                        }
                        is MonitoringState.OnSuccessSaveBuffer -> {
                            val message = StringUtils.getErrorMessage(
                                requireContext().getString(R.string.total_buffer),
                                requireContext().getString(R.string.success_change),
                                requireContext()
                            )
                            showNotification(message, R.color.success_0)
                        }
                        is MonitoringState.OnFailedSaveBuffer -> {
                            val message = StringUtils.getErrorMessage(
                                requireContext().getString(R.string.total_buffer),
                                requireContext().getString(R.string.failed_change) + " - ${it.message}" ,
                                requireContext()
                            )
                            showNotification(message, R.color.warning_0)
                        }
                        MonitoringState.SearchScreen -> {
                            findNavController().navigate(R.id.monitoringFragmentSearch)
                        }
                        else -> {
                            //do noting
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
                    else -> {
                        //do nothing
                    }
                }
                true
            }
            R.id.searchScreen -> {
                monitoringViewModel.searchScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.monitoring_action, menu)
    }

    private fun initTable() {
        tableAdapter = AdapterMonitoringTable(monitoringViewModel)
        tableAdapter.setHeaderLabel(requireContext().resources.getStringArray(R.array.column_header).toList())
        mBinding.tableView.apply {
            setAdapter(tableAdapter)
        }
    }

    private fun showNotification(message : Spanned, color : Int){
        DialogUtils.showSnackbar(
            requireView(),
            requireContext(),
            message,
            color,
            null
        )
    }

}