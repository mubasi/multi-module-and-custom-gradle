package id.bluebird.vsm.feature.queue_fleet.request_fleet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.databinding.RequestFleetDialogBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRequestFleetDialog(
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
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            lifecycleOwner = viewLifecycleOwner
            vm = _mRequestFleetDialogViewModel
            counterView.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    _mRequestFleetDialogViewModel.requestFleet()
                    return@setOnEditorActionListener true
                }
                false
            }
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
                            showSnackbar(Html.fromHtml("<b>Request Armada</b> berhasil ditambahkan",1), R.color.success_0)
                            dialog?.dismiss()
                        }
                        is RequestFleetDialogState.Err -> {
                            Toast.makeText(requireContext(), it.err.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        is RequestFleetDialogState.MessageError -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        is RequestFleetDialogState.FocusState -> {
                            if (it.isFocus.not()) {
                                val imm: InputMethodManager = mBinding.root.context
                                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(mBinding.root.windowToken, 0)
                            }
                            mBinding.counterView.isCursorVisible = it.isFocus
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }

    fun showSnackbar(message: Spanned, color: Int){
        val snackbar = Snackbar.make(requireActivity().window.decorView.rootView,message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(-10,160,-10,0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), color))
        snackbar.show()
    }
}