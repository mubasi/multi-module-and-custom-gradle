package id.bluebird.vsm.feature.home.dialog_record_ritase

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.DialogRitaseRecordBinding
import id.bluebird.vsm.feature.home.model.CurrentQueueCache
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDialogRecordRitase(
    locationId : Long,
    subLocationId : Long,
    queue : CurrentQueueCache,
    fleetNumber : String,
    callBackProsess: (fleetNumber: String, queueNumber: String) -> Unit
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "dialogRecordRitase"
    }

    private lateinit var binding: DialogRitaseRecordBinding
    private val vm: DialogRecordRitaseViewModel by viewModel()
    private val _queue = queue
    private val _locationId = locationId
    private val _subLocationId = subLocationId
    private val _fleetNumber = fleetNumber
    private var chooseFleet = callBackProsess

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_ritase_record, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        vm.init(
            queue = _queue,
            valLocationId = _locationId,
            valSubLocationId = _subLocationId,
            valFleetNumber = _fleetNumber
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(vm) {
                    action.collect {
                        when(it) {
                            DialogRecordRitaseState.CancelDialog -> {
                                dialog?.dismiss()
                            }
                            is DialogRecordRitaseState.SelectFleet -> {
                                dialog?.dismiss()
                                findNavController().navigate(R.id.ritaseFleetFragment)
                            }
                            DialogRecordRitaseState.FleetEmpty -> {
                                val title = requireContext().getString(R.string.title_fleet_not_selected)
                                val msg = requireContext().getString(R.string.msg_fleet_not_selected)
                                showDialogInfo(title, msg)
                            }
                            is DialogRecordRitaseState.SuccessRitase -> {
                                chooseFleet(it.fleetNumber, it.queueNumber)
                                dialog?.dismiss()
                            }
                            is DialogRecordRitaseState.OnError -> {
                                val title = requireContext().getString(R.string.ritase_failed_title)
                                val msg = "Armada ${it.fleetNumber} gagal di ritase"
                                showDialogInfo(title, msg)
                            }
                            else -> {
                                //do nothing
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialogInfo(title :String, message : String) {
        DialogUtils.showErrorDialog(
            requireContext(),
            title = title,
            message = message,
        )
    }

}