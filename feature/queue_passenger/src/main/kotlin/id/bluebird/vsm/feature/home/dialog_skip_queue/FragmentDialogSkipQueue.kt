package id.bluebird.vsm.feature.home.dialog_skip_queue

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.home.databinding.DialogSkipQueueBinding
import id.bluebird.vsm.feature.home.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDialogSkipQueue(
    callBackProcess: () -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "requestDialogSkipQueue"
    }

    private lateinit var binding : DialogSkipQueueBinding
    private val _dialogSkipQueueViewModel : DialogSkipQueueViewModel by viewModel()
    private val chooseProcess = callBackProcess

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_skip_queue, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = _dialogSkipQueueViewModel

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        val queueId = arguments?.getLong("queue_id") ?: 0
        val number = arguments?.getString("number") ?: ""
        val locationId = arguments?.getLong("location_id") ?: 0
        val subLocationId = arguments?.getLong("sub_location_id") ?: 0

        var setText = Html.fromHtml("Lewati antrian <font color=#005EB8>$number</font> ?", 1)

        binding.textButtonSeed.setText(setText)

        binding.showProses = false;

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_dialogSkipQueueViewModel) {
                    dialogSkipQueueState.collect {
                        when(it) {
                            DialogSkipQueueState.CancleDialog -> {
                                dialog?.dismiss()
                                binding.showProses = false;
                            }
                            DialogSkipQueueState.ProsessDialog -> {
                                prosesSkipQueue(queueId, locationId, subLocationId)
                                binding.showProses = true;
                            }
                            DialogSkipQueueState.SuccessDialog -> {
                                dialog?.dismiss()
                                showSnackbar(Html.fromHtml("<b>No. antrian $number telah ditunda</b>",1), R.color.error_color_second)
                                chooseProcess()
                            }
                            is DialogSkipQueueState.FailedDialog -> {
                                showSnackbar(Html.fromHtml("<b>No. antrian $number tidak dapat diproses</b>",1), R.color.error_color)
                            }
                        }
                    }
                }
            }
        }
    }

    fun showSnackbar(message: Spanned, color: Int){
        DialogUtils.showSnackbar(requireView(), message, color)
    }



}