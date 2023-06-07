package id.bluebird.vsm.feature.home.dialog_restore_skipped

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
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.DialogRestoreSkippedQueueBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDialogRestoreSkipped(
    private val number: String,
    private val queueId: Long,
    private val locationId: Long,
    private val subLocationId: Long
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "requestDialogRestoreSkipped"
    }
    private lateinit var binding: DialogRestoreSkippedQueueBinding
    private val dialogRestoreSkippedViewModel: DialogRestoreSkippedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_restore_skipped_queue, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = dialogRestoreSkippedViewModel

        binding.showProses = false
        binding.titleDialog.text = Html.fromHtml("<b>Pulihkan antrean <font color=#005EB8>$number</font>?</b>", 1)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(dialogRestoreSkippedViewModel) {
                    dialogRestoreSkippedState.collect {
                        when(it) {
                            is DialogRestoreSkippedState.FailedRestoreQueueSkipped -> {
                                dialog?.dismiss()
                                showSnackbar(Html.fromHtml("<b>No. antrean $number</b> gagal dipulihkan. Silahkan coba lagi",1), R.color.error_color_second)
                            }
                            DialogRestoreSkippedState.ProsesRestoreQueueSkipped -> {
                                binding.showProses = true
                                prosesRestoreQueue(number, queueId, locationId, subLocationId)
                            }
                            DialogRestoreSkippedState.CancelRestoreQueueSkipped -> {
                                dialog?.dismiss()
                            }
                            DialogRestoreSkippedState.SuccessRestoreQueueSkipped -> {
                                dialog?.dismiss()
                                showSnackbar(Html.fromHtml("<b>No. antrean $number</b> telah dipulihkan",1), R.color.success_color)
                                findNavController().navigate(R.id.queuePassengerFragment)
                            }
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