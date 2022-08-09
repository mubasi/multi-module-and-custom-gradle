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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.snackbar.Snackbar
import id.bluebird.mall.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.mall.home.R
import id.bluebird.mall.home.dialog_queue_receipt.DialogQueueReceipt
import id.bluebird.mall.home.dialog_skip_queue.DialogSkipQueueFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager


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

        binding.showData = false
        binding.successCurrentQueue = false
        binding.successListQueue = false

        _queuePassengerViewModel.init()
        setupTabLayout()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_queuePassengerViewModel) {
                    queuePassengerState.collect {
                        when(it) {
                            QueuePassengerState.ProsesGetUser -> {
                                binding.showData = false
                            }
                            QueuePassengerState.ProsesQueue -> {
                                DialogQueueReceipt().show(childFragmentManager, DialogQueueReceipt.TAG)
                            }
                            QueuePassengerState.SuccessGetUser -> {
                                binding.showData = false
                                getCurrentQueue()
                            }
                            QueuePassengerState.SuccessCurrentQueue -> {
                                binding.showData = true
                                binding.successCurrentQueue = true
                                getListQueue()
                            }
                            is QueuePassengerState.FailedCurrentQueue -> {
                                binding.showData = true
                                binding.successCurrentQueue = false
                            }
                            QueuePassengerState.ProsesListQueue -> {
                                binding.showData = false
                                binding.successListQueue = false
                            }
                            QueuePassengerState.SuccessListQueue -> {
                                binding.showData = true
                                binding.successListQueue = true
                                if(listQueueWaitingCache.count == 0L) {
                                    binding.successListQueue = false
                                }
                                setupListQueue()
                            }
                            QueuePassengerState.ProsesSkipQueue -> {
                                val bundle = Bundle()
                                bundle.putLong("queue_id", currentQueueCache.id)
                                bundle.putString("number", currentQueueCache.number)
                                bundle.putLong("location_id", mUserInfo.locationId)
                                bundle.putLong("sub_location_id", mUserInfo.subLocationId)

                                val dialogSkipQueue = DialogSkipQueueFragment()
                                dialogSkipQueue.arguments = bundle
                                dialogSkipQueue.show(requireActivity().supportFragmentManager, DialogSkipQueueFragment.TAG)
                            }
                            else -> {
                                //do noting
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListQueue() {

        val tab0: TabLayout.Tab? = binding.tabLayout.getTabAt(0)

        val countWaiting = _queuePassengerViewModel.listQueueWaitingCache.count
        tab0?.text = getString(R.string.tab_waiting_text) + " ($countWaiting)"

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CustomAdapter(_queuePassengerViewModel.listQueueWaitingCache.queue)
        binding.recyclerView.adapter = adapter
    }


    private fun setupTabLayout() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Write code to handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Write code to handle tab reselect
            }
        })
    }
}