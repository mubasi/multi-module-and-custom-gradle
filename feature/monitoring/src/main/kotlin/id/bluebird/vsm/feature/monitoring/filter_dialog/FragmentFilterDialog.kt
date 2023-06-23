package id.bluebird.vsm.feature.monitoring.filter_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.FragmentFilterDialogBinding
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFilterDialog(
    currentStatus : MonitoringViewModel.FilterStatus,
    callBackProcess: (result : MonitoringViewModel.FilterStatus) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FragmentFilterDialog"
    }

    private lateinit var binding : FragmentFilterDialogBinding
    private val viewModel : FilterDialogViewModel by viewModel()
    private var chooseFleet = callBackProcess
    private var statusFilter = currentStatus

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_dialog, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        setupDialog()
        observe()
        setupChipGroup()
        setActiveChip()
    }

    private fun setupDialog() {
        dialog?.let {
            it.setCancelable(false)
            it.setContentView(binding.root)
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(viewModel) {
                    state.collectLatest {
                        binding.state = it
                        when (it) {
                            is FilterDialogState.SaveFilter -> {
                                chooseFleet(it.result)
                                dialog?.dismiss()
                            }
                            is FilterDialogState.CloseFilter -> {
                                dialog?.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupChipGroup() {
        with(binding) {
            filterPage.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    filterWaiting.id -> {
                        viewModel.setStatusFilter(
                            MonitoringViewModel.FilterStatus.DEPOSITION
                        )
                    }
                    filterSkipped.id -> {
                        viewModel.setStatusFilter(
                            MonitoringViewModel.FilterStatus.LOBBY
                        )
                    }
                    else -> {
                        viewModel.setStatusFilter(
                            MonitoringViewModel.FilterStatus.ALL
                        )
                    }
                }
            }
        }
    }

    private fun setActiveChip() {
        binding.filterAll.isChecked = statusFilter == MonitoringViewModel.FilterStatus.ALL
        binding.filterWaiting.isChecked = statusFilter == MonitoringViewModel.FilterStatus.DEPOSITION
        binding.filterSkipped.isChecked = statusFilter == MonitoringViewModel.FilterStatus.LOBBY
    }

}