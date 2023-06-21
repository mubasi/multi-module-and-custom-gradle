package id.bluebird.vsm.feature.queue_car_fleet.depart_fleet

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
import com.ncorti.slidetoact.SlideToActView
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.DepartCarFleetDialogBinding
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDepartCarFleetDialog(
    private val fleet: CarFleetItem,
    private val locationId: Long,
    private val subLocationId: Long,
    private val callback: (carFleetItem: CarFleetItem, isWithPassenger: Boolean, queueNumber: String) -> Unit,
    private val onError: (throwable: Throwable) -> Unit
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FragmentDepartFleetDialog"
        const val DISMISS = "DISMISS"
    }

    private lateinit var mBinding: DepartCarFleetDialogBinding
    private val _departViewModel: DepartCarFleetViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.depart_car_fleet_dialog, container, false)
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
            locationId = this@FragmentDepartCarFleetDialog.locationId
            subLocationId = this@FragmentDepartCarFleetDialog.subLocationId
        }
        _departViewModel.init(fleet)
        dialog?.apply {
            setCancelable(false)
            setContentView(view)
        }
        setupSlideProses()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _departViewModel.sharedDepartFleetState.collectLatest {
                    mBinding.showProgress = it == DepartCarFleetState.OnProgressGetCurrentQueue
                    when (it) {
                        is DepartCarFleetState.CancelDepartCar -> {
                            onError(Throwable(DISMISS))
                            dialog?.dismiss()
                        }
                        is DepartCarFleetState.DepartCarFleet -> {
                            callback(it.carFleetItem, it.isWithPassenger, it.currentQueueNumber)
                            dialog?.dismiss()
                        }
                        is DepartCarFleetState.OnFailed -> {
                            onError(it.throwable)
                            dialog?.dismiss()
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }
    private fun setupSlideProses()  {
        mBinding.layoutSlideProses.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                _departViewModel.departFleet()
            }
        }
    }
}