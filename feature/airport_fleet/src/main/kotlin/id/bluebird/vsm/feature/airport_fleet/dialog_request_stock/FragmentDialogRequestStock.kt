package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.DialogBottomRequestStockFleetBinding
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDialogRequestStock(
    private val subLocationId: Long,
    private val subLocationName: String,
    private val requestToId : Long,
    private val callback: (count: Long) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val MINIMUM_REQUEST_TAXI = 1L
        const val CANCEL_VALUE = -1L
        const val TAG = "requestStock"
    }

    private lateinit var binding: DialogBottomRequestStockFleetBinding
    private val vm: DialogButtomRequestStockViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_bottom_request_stock_fleet, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.SheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vmPerimeter = vm

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        vm.initSubLocationId(subLocationId = subLocationId, requestToId = requestToId)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(vm){
                    action.collect {
                        when(it) {
                            DialogRequestStockState.CancleDialog -> {
                                callback(CANCEL_VALUE)
                                dialog?.dismiss()
                            }
                            is DialogRequestStockState.Err -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    message = it.err.message ?: getString(R.string.error_unknown),
                                    title = getString(R.string.request_fleet_failed)
                                )
                            }
                            is DialogRequestStockState.FocusState -> {
                                if (it.isFocus.not()) {
                                    val imm: InputMethodManager = binding.root.context
                                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                                }
                                binding.counterView.isCursorVisible = it.isFocus
                            }
                            is DialogRequestStockState.RequestSuccess -> {
                                callback(it.count)
                                val message = SpannableStringBuilder()
                                    .append("$subLocationName ${getString(R.string.total_request_on_terminal)} ${it.count} ")
                                DialogUtils.showSnackbar(view, message, R.color.success_0)
                                delay(3000)
                                dialog?.dismiss()
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




}