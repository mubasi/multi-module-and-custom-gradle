package id.bluebird.vsm.feature.home.queue_ticket

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.core.ui.BaseFragment
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.QueueTicketBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class QueueTicket : BaseFragment() {

    companion object {
        const val TAG = "requestQueueTicket"
    }

    private lateinit var binding: QueueTicketBinding
    private val _queueTicketViewModel: QueueTicketViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.queue_ticket, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = _queueTicketViewModel


        val queueNumber = arguments?.getString("queueNumber") ?: ""
        val createdAt = arguments?.getString("createdAt") ?: ""
        val currentNumber = arguments?.getString("currentNumber") ?: ""
        val totalQueue = arguments?.getString("totalQueue") ?: ""

        formatDate(createdAt)

        binding.currentNumber.text = currentNumber
        binding.queueNumber.text = queueNumber
        binding.totalQueue.text = totalQueue

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        showSnackbar(Html.fromHtml("<b>No. antrian $queueNumber telah ditambahkan</b>",1), R.color.success_color)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _queueTicketViewModel.queueTicketState.collect{
                    with(_queueTicketViewModel) {
                        when(it) {
                            QueueTicketState.ProsesTicket -> {
                                findNavController().popBackStack()
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

    fun formatDate(inputDate: String) {
        var convertedDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ", Locale.getDefault())
        try {
            convertedDate = dateFormat.parse(inputDate)
        } catch (ignored: ParseException) {
        }

        val dfOutput = SimpleDateFormat("HH:mm", Locale.getDefault())
        val str :String = dfOutput.format(convertedDate)
        binding.timeOrder.text = str
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