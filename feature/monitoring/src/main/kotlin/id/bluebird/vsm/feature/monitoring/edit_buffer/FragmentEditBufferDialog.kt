package id.bluebird.vsm.feature.monitoring.edit_buffer

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
import id.bluebird.vsm.core.extensions.hideSoftKeyboard
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.SetBufferDialogBinding
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentEditBufferDialog(
    private val item: MonitoringModel,
    private val resultCallback: (isSuccess: Boolean, failedMessage: String?) -> Unit
): BottomSheetDialogFragment() {
    companion object {
        const val TAG = "FragmentEditBufferDialog"
    }

    private lateinit var mBinding: SetBufferDialogBinding
    private val editBufferViewModel: EditBufferViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.set_buffer_dialog, container, false)
        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = editBufferViewModel
        }

        dialog?.apply {
            setCancelable(false)
            setContentView(view)
        }

        editBufferViewModel.init(item)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                editBufferViewModel.editBufferState.collectLatest {
                    when(it) {
                        is EditBufferState.FocusState -> {
                            if (!it.isFocus) {
                                requireContext().hideSoftKeyboard(view)
                                mBinding.counterView.clearFocus()
                            }
                        }
                        is EditBufferState.ClosingDialog -> {
                            dialog?.dismiss()
                        }
                        is EditBufferState.SuccessSave -> {
                            resultCallback.invoke(true, null)
                            dialog?.dismiss()
                        }
                        is EditBufferState.FailedSave -> {
                            resultCallback.invoke(false, "Failed")
                            dialog?.dismiss()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}