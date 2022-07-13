package id.bluebird.mall.feature_queue_fleet.request_fleet

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
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.databinding.RequestFleetDialogBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestFleetDialog(
    private val subLocationId: Long,
    private val callback: (count: Long) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "requestTag"
    }

    private lateinit var mBinding: RequestFleetDialogBinding
    private val _mRequestFleetDialogViewModel: RequestFleetDialogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.request_fleet_dialog, container, false)
        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.SheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            lifecycleOwner = viewLifecycleOwner
            vm = _mRequestFleetDialogViewModel
        }
        _mRequestFleetDialogViewModel.initSubLocationId(subLocationId = subLocationId)

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _mRequestFleetDialogViewModel.requestFleetDialogState.collect {
                    when (it) {
                        RequestFleetDialogState.CancelDialog -> {
                            dialog?.dismiss()
                        }
                        is RequestFleetDialogState.RequestSuccess -> {
                            callback(it.count)
                            dialog?.dismiss()
                        }
                        is RequestFleetDialogState.Err -> {
                            Toast.makeText(requireContext(), it.err.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        is RequestFleetDialogState.MessageError -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }
}