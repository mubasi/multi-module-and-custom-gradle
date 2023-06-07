package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDialogRequestStock(
    private val subLocationId: Long,
    private val requestToId : Long,
    private val callback: (count: Long) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
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
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        validateCounterView()
        setConditionDialog(view)
        setInit()
        setObserve(view)
    }

    private fun setBinding() {
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vmPerimeter = vm
        }
    }

    private fun setConditionDialog(view : View) {
        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }
    }

    private fun setInit() {
        vm.initSubLocationId(
            subLocationId = subLocationId,
            requestToId = requestToId
        )
    }

    private fun setObserve(view : View) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(vm){
                    action.collect {
                        when(it) {
                            DialogRequestStockState.CancleDialog -> {
                                callback(CANCEL_VALUE)
                                dialog?.dismiss()
                                showProgressState(false)
                            }
                            is DialogRequestStockState.Err -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    message = it.err.message ?: getString(R.string.error_unknown),
                                    title = getString(R.string.request_fleet_failed)
                                )
                                showProgressState(false)
                            }
                            is DialogRequestStockState.FocusState -> {
                                if (it.isFocus.not()) {
                                    val imm: InputMethodManager = binding.root.context
                                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                                }
                                binding.counterView.isCursorVisible = it.isFocus
                                showProgressState(false)
                            }
                            is DialogRequestStockState.MessageError -> {
                                val message = SpannableStringBuilder()
                                    .append(it.message)
                                showNotify(view, message, R.color.warning_color)
                                showProgressState(false)
                            }
                            is DialogRequestStockState.RequestSuccess -> {
                                callback(it.count)
                                dialog?.dismiss()
                                showProgressState(false)
                            }
                            DialogRequestStockState.SendRequestTaxiOnProgress -> {
                                showProgressState(true)
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
    private fun showProgressState(progress : Boolean) {
        binding.showProgress = progress
    }


    private fun showNotify(view: View, message : Spanned, background : Int) {
        DialogUtils.showSnackbar(view, requireContext(), message, background, null)
    }

    private fun validateCounterView() {
        binding.counterView.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                vm.sendFleetRequest()
                return@setOnEditorActionListener true
            }
            false
        }
    }
}