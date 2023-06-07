package id.bluebird.vsm.feature.queue_car_fleet.ritase_record

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.RecordRitaseCarFleetDialogBinding
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.DepartCarFleetState
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRitaseCarFleetRecordDialog(
    private val carFleetItem: CarFleetItem,
    private val queueNumber: String,
    private val locationId: Long,
    private val subLocationId: Long,
    private val departCallback: ((carFleetItem: CarFleetItem, isWithPassenger: Boolean, queueNumber: String) -> Unit)?,
    private val showQueueListCallback: ((carFleetItem: CarFleetItem, currentQueueNumber: String, locationId: Long, subLocationId: Long) -> Unit)?
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ritaseRecordTag"
    }

    private lateinit var mBinding: RecordRitaseCarFleetDialogBinding
    private val _departViewModel: RitaseCarFleetRecordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.record_ritase_car_fleet_dialog, container, false)
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
            queueNumber = this@FragmentRitaseCarFleetRecordDialog.queueNumber
            locationId = this@FragmentRitaseCarFleetRecordDialog.locationId
            subLocationId = this@FragmentRitaseCarFleetRecordDialog.subLocationId
            showProgress = false
        }
        _departViewModel.init(carFleetItem, queueNumber, locationId, subLocationId)
        dialog?.apply {
            setCancelable(false)
            setContentView(view)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _departViewModel.sharedDepartFleetState.collect {
                    when (it) {
                        is DepartCarFleetState.CancelDepartCar -> {
                            dialog?.dismiss()
                        }
                        is DepartCarFleetState.DepartCarFleet -> {
                            departCallback?.invoke(it.carFleetItem, it.isWithPassenger, it.currentQueueNumber)
                            dialog?.dismiss()
                        }
                        is DepartCarFleetState.SelectQueueToDepartCar -> {
                            showQueueListCallback?.invoke(
                                it.carFleetItem,
                                it.currentQueueId,
                                it.locationId,
                                it.subLocationId
                            )
                        }
                        is DepartCarFleetState.SuccessGetCurrentQueue -> {
                            mBinding.queueNumber = it.queueId
                        }
                        is DepartCarFleetState.OnFailedGetCurrentQueue -> {
                            Toast.makeText(
                                requireContext(),
                                "${it.throwable.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // do nothing
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