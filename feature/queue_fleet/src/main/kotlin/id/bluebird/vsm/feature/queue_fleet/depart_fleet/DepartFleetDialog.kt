package id.bluebird.vsm.feature.queue_fleet.depart_fleet

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
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.databinding.DepartFleetDialogBinding
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DepartFleetDialog(
    private val fleet: FleetItem,
    private val callback: (fleetItem: FleetItem, isWithPassenger: Boolean, queueNumber: String) -> Unit
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "DepartFleetDialog"
    }

    private lateinit var mBinding: DepartFleetDialogBinding
    private val _departViewModel: DepartFleetViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.depart_fleet_dialog, container, false)
        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            lifecycleOwner = viewLifecycleOwner
            vm = _departViewModel
            title = getString(R.string.depart_description, fleet.name)
            fleetNumber = fleet.name
            queueNumber = ""
        }
        _departViewModel.init(fleet)
        dialog?.apply {
            setCancelable(false)
            setContentView(view)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _departViewModel.sharedDepartFleetState.collect {
                    when (it) {
                        is DepartFleetState.CancelDepart -> {
                            dialog?.dismiss()
                        }
                        is DepartFleetState.DepartFleet -> {
                            callback(it.fleetItem, it.isWithPassenger, it.currentQueueNumber)
                            dialog?.dismiss()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}