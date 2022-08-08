package id.bluebird.mall.home.dialog_queue_receipt

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import com.ncorti.slidetoact.SlideToActView
import id.bluebird.mall.core.ui.BaseFragment
import id.bluebird.mall.core.utils.hawk.AuthUtils
import id.bluebird.mall.home.databinding.DialogQueueReceiptBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import id.bluebird.mall.home.R
import id.bluebird.mall.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.mall.navigation.NavigationNav
import id.bluebird.mall.navigation.NavigationSealed
import kotlinx.coroutines.launch

class DialogQueueReceipt :  BottomSheetDialogFragment() {

    companion object {
        const val TAG = "requestQueueReceipt"
    }

    private lateinit var binding: DialogQueueReceiptBinding
    private val _dialogQueueReceiptViewModel: DialogQueueReceiptViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_queue_receipt, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = _dialogQueueReceiptViewModel

        dialog?.let {
            it.setCancelable(false)
            it.setContentView(view)
        }

        binding.slideProses.visibility = View.GONE
        binding.textQueue.visibility = View.GONE
        binding.progressDialog.visibility = View.VISIBLE

        binding.slideProses.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                _dialogQueueReceiptViewModel.requestQueue()
            }
        }

        _dialogQueueReceiptViewModel.init()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_dialogQueueReceiptViewModel) {
                    dialogQueueReceiptState.collect{
                        when(it) {
                            is DialogQueueReceiptState.FailedGetUser -> {
                                showSnackbar( Html.fromHtml("Gagal Mengambil Informasi User",1), R.color.error_color)
                                dialog?.dismiss()
                            }
                            is DialogQueueReceiptState.FailedTakeQueue -> {
                                val noAntrian : String = queueNumber.value.toString();
                                showSnackbar(Html.fromHtml("<b>$noAntrian</b> gagal ditambahkan",1), R.color.error_color)
                                dialog?.dismiss()
                                binding.slideProses.visibility = View.VISIBLE
                                binding.textQueue.visibility = View.VISIBLE
                                binding.progressDialog.visibility = View.GONE
                            }
                            is DialogQueueReceiptState.FailedGetQueue -> {
                                binding.slideProses.visibility = View.GONE
                                binding.textQueue.visibility = View.GONE
                                binding.progressDialog.visibility = View.VISIBLE
                                showSnackbar(Html.fromHtml("Antrian tidak dapat diambil",1), R.color.error_color)
                                dialog?.dismiss()
                            }
                            is DialogQueueReceiptState.ProgressGetQueue -> {
                                binding.slideProses.visibility = View.GONE
                                binding.textQueue.visibility = View.GONE
                                binding.progressDialog.visibility = View.VISIBLE
                            }
                            DialogQueueReceiptState.ProgressGetUser -> {
                                binding.slideProses.visibility = View.GONE
                                binding.textQueue.visibility = View.GONE
                                binding.progressDialog.visibility = View.VISIBLE
                            }
                            DialogQueueReceiptState.GetUserInfoSuccess -> {
                                getQueue()
                            }
                            DialogQueueReceiptState.GetQueueSuccess -> {
                                binding.slideProses.visibility = View.VISIBLE
                                binding.textQueue.visibility = View.VISIBLE
                                binding.progressDialog.visibility = View.GONE
                            }
                            DialogQueueReceiptState.CancelDialog -> {
                                dialog?.dismiss()
                            }
                            DialogQueueReceiptState.TakeQueueSuccess -> {
                                dialog?.dismiss()
                                val bundle = Bundle()
                                bundle.putString("queueNumber", takeQueueCache.queueNumber)
                                bundle.putString("currentNumber", takeQueueCache.currentQueue)
                                bundle.putString("totalQueue", takeQueueCache.totalQueue.toString())
                                bundle.putString("createdAt", takeQueueCache.createdAt)
                                findNavController().navigate(R.id.queueTicket, bundle)
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

    fun showSnackbar(message: Spanned, color: Int){
        val snackbar = Snackbar.make(requireActivity().window.decorView.rootView,message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(-10,50,-10,0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), color))
        snackbar.show()
    }
}