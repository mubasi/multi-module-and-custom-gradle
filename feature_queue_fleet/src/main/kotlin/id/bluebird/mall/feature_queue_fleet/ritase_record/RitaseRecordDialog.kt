package id.bluebird.mall.feature_queue_fleet.ritase_record

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetFragment
import id.bluebird.mall.feature_queue_fleet.databinding.RecordRitaseDialogBinding
import id.bluebird.mall.feature_queue_fleet.depart_fleet.DepartFleetState
import id.bluebird.mall.feature_queue_fleet.depart_fleet.DepartFleetViewModel
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetFragmentDirections
import id.bluebird.mall.feature_queue_fleet.model.FleetItem
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RitaseRecordDialog(
    private val fleetItem: FleetItem,
    private val queueNumber: String,
    private val subLocationId: Long,
    private val departCallback: ((fleetItem: FleetItem, isWithPassenger: Boolean, queueNumber: String) -> Unit)?,
    private val showQueueListCallback: ((fleetItem: FleetItem, currentQueueNumber: String) -> Unit)?
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ritaseRecordTag"
    }

    private lateinit var mBinding: RecordRitaseDialogBinding
    private val _departViewModel: DepartFleetViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.record_ritase_dialog, container, false)
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
            queueNumber = this@RitaseRecordDialog.queueNumber
        }
        _departViewModel.init(fleetItem)
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
                            departCallback?.invoke(it.fleetItem, it.isWithPassenger, it.currentQueueNumber)
                            dialog?.dismiss()
                        }
                        is DepartFleetState.SelectQueueToDepart -> {
                            showQueueListCallback?.invoke(it.fleetItem, it.currentQueueId)
                        }
                    }
                }
            }
        }
    }

    fun updateQueue(queueNumber: String) {
        mBinding.queueNumber = queueNumber
    }
}