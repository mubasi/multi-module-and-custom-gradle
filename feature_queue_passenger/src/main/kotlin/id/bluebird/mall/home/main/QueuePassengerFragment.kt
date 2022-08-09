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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.snackbar.Snackbar
import id.bluebird.mall.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.mall.home.R
import id.bluebird.mall.home.databinding.FragmentQueuePassengerBinding
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
        savedInstanceState: Bundle?,
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
        setupListQueue()

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
                                setupFirst()
                            }
                            is QueuePassengerState.FailedCurrentQueue -> {
                                binding.showData = true
                                binding.successCurrentQueue = false
                                setupFirst()
                            }
                            QueuePassengerState.ProsesListQueue -> {
                                binding.showData = false
                            }
                            is QueuePassengerState.FailedListQueue -> {
                                setupWaiting()
                                binding.showData = true
                                binding.successListQueue = false
                            }
                            QueuePassengerState.SuccessListQueue -> {
                                setupWaiting()
                                binding.showData = true
                            }
                            QueuePassengerState.ProsesListQueueSkipped -> {
                                binding.showData = false
                            }
                            is QueuePassengerState.FailedListQueueSkipped -> {
                                setupSkipped()
                                binding.showData = true
                                binding.successListQueue = false
                            }
                            QueuePassengerState.SuccessListQueueSkipped -> {
                                setupSkipped()
                                binding.showData = true
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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFirst() {
        _queuePassengerViewModel.getListQueue()
        _queuePassengerViewModel.getListQueueSkipped()
        val selectedPosition = binding.tabLayout.selectedTabPosition
        setListQueue(selectedPosition)
        binding.successListQueue = false
    }

    private fun setupWaiting() {
        val tab0: TabLayout.Tab? = binding.tabLayout.getTabAt(0)
        val countWaiting = _queuePassengerViewModel.listQueueWaitingCache.count
        tab0?.text = "Menunggu ($countWaiting)"
        val adapter = CustomAdapter(_queuePassengerViewModel.listQueueWaitingCache.queue)
        binding.recyclerView.adapter = adapter

        if(countWaiting > 0L) {
            binding.successListQueue = true
        }
    }

    private fun setupSkipped() {
        val tab1: TabLayout.Tab? = binding.tabLayout.getTabAt(1)
        val countSkipped = _queuePassengerViewModel.listQueueSkippedCache.count
        tab1?.text = "Tertunda ($countSkipped)"

        if(countSkipped > 0L) {
            binding.successListQueue = true
        }
    }

    private fun setListQueue(position: Int) {
        binding.successListQueue = false
        if(position == 0) {
            setupWaiting()
        } else if(position == 1) {
            setupSkipped()
            val adapter = CustomAdapterSkipped(_queuePassengerViewModel.listQueueSkippedCache.queue)
            binding.recyclerView.adapter = adapter
        }
    }
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
                tab?.position?.let { setListQueue(it) }
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