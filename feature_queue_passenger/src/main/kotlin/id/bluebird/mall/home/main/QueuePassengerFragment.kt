package id.bluebird.mall.home.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import id.bluebird.mall.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.mall.home.R
import id.bluebird.mall.home.dialog_queue_receipt.DialogQueueReceipt
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueuePassengerFragment : Fragment() {

    private lateinit var binding : FragmentQueuePassengerBinding
    private val _queuePassengerViewModel : QueuePassengerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue_passenger, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = _queuePassengerViewModel

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_queuePassengerViewModel) {
                    queuePassengerState.collect {
                        when(it) {
                            QueuePassengerState.ProsesQueue -> {
                                DialogQueueReceipt().show(childFragmentManager, DialogQueueReceipt.TAG)
                            }
                        }
                    }
                }
            }
        }
    }
}