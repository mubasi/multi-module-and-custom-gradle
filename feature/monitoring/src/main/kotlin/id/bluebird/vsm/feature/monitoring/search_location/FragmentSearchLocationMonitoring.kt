package id.bluebird.vsm.feature.monitoring.search_location

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.core.utils.StringUtils
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.FragmentSearchMonitoringBinding
import id.bluebird.vsm.feature.monitoring.edit_buffer.FragmentEditBufferDialog
import id.bluebird.vsm.feature.monitoring.main.MonitoringState
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.monitoring.tableview.AdapterMonitoringTable
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSearchLocationMonitoring : Fragment() {
    private lateinit var mBinding: FragmentSearchMonitoringBinding
    private lateinit var tableAdapter: AdapterMonitoringTable
    private val monitoringViewModel: MonitoringViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_monitoring, container, false)
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
        initTable()
        setupFilter()
        visibleIconClear(false)
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
                            DialogUtils.showSnackbar(view, SpannableStringBuilder("Error when getting data"), R.color.warning_0)
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
                        is MonitoringState.FilterLocation -> {
                            tableAdapter.setItem(it.data)
                        }
                        MonitoringState.BackSearchScreen -> {
                            findNavController().popBackStack()
                        }
                        is MonitoringState.ErrorFilter -> {
                            showErrorMassage()
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
            }
        }
    }

    private fun initTable() {
        tableAdapter = AdapterMonitoringTable(monitoringViewModel)
        tableAdapter.setHeaderLabel(requireContext().resources.getStringArray(R.array.column_header).toList())
        mBinding.tableView.apply {
            setAdapter(tableAdapter)
        }
    }

    private fun setupFilter(){
        mBinding.searchForm.doOnTextChanged { text, _, _, _ ->
            monitoringViewModel.params.value = text.toString()
            monitoringViewModel.filterLocation()
            visibleIconClear(text?.isNotEmpty() ?: false)
        }
        mBinding.clearSearch.setOnClickListener {
            mBinding.searchForm.text?.clear()
            monitoringViewModel.clearSearch()
            visibleIconClear(false)
        }

    }

    private fun visibleIconClear(result : Boolean){
        mBinding.clearSearch.isVisible = result
    }

    private fun showNotification(message : Spanned, color : Int){
        DialogUtils.showSnackbar(
            requireView(),
            message,
            color
        )
    }

    private fun showErrorMassage(){
        val title =
            requireContext().getString(R.string.title_not_found_location)
        val msg =
            requireContext().getString(R.string.msg_not_found_location)
        DialogUtils.showErrorDialog(requireContext(), title, msg)
    }
}