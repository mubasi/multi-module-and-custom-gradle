package id.bluebird.vsm.feature.home.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.vsm.feature.home.dialog_queue_receipt.FragmentDialogQueueReceipt
import id.bluebird.vsm.feature.home.dialog_skip_queue.FragmentDialogSkipQueue
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.home.dialog_delete_skipped.FragmentDialogDeleteSkipped
import id.bluebird.vsm.feature.home.dialog_restore_skipped.FragmentDialogRestoreSkipped
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed


class FragmentQueuePassenger : Fragment() {

    private lateinit var binding: FragmentQueuePassengerBinding
    private val _queuePassengerViewModel: QueuePassengerViewModel by viewModel()
    private var positionType: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_queue_passenger, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = _queuePassengerViewModel

        binding.showData = false
        binding.successCurrentQueue = false
        binding.successListQueue = false

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        setupTabLayout()
        setupListQueue()
        setupSwipeRefresh()
        observer()
        _queuePassengerViewModel.init()

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_queuePassengerViewModel) {
                    queuePassengerState.collect {
                        when (it) {
                            QueuePassengerState.ToSelectLocation -> {
                                NavigationNav.navigate(
                                    NavigationSealed.SelectLocation(
                                        destination = null,
                                        frag = this@FragmentQueuePassenger,
                                        isMenuFleet = false
                                    )
                                )
                            }
                            QueuePassengerState.ProsesGetUser -> {
                                binding.showData = false
                                binding.swipeRefreshLayout.isRefreshing = false
                            }
                            QueuePassengerState.ProsesQueue -> {
                                FragmentDialogQueueReceipt().show(
                                    childFragmentManager,
                                    FragmentDialogQueueReceipt.TAG
                                )
                            }
                            QueuePassengerState.SuccessGetUser -> {
                                binding.showData = false
                                binding.swipeRefreshLayout.isRefreshing = false
                                getCounterBar()
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
                                binding.swipeRefreshLayout.isRefreshing = false
                            }
                            is QueuePassengerState.FailedListQueue -> {
                                setListQueue(binding.tabLayout.selectedTabPosition)
                                binding.showData = true
                                binding.successListQueue = false
                            }
                            QueuePassengerState.SuccessListQueue -> {
                                setListQueue(binding.tabLayout.selectedTabPosition)
                                binding.showData = true
                            }
                            QueuePassengerState.ProsesListQueueSkipped -> {
                                binding.showData = false
                                binding.swipeRefreshLayout.isRefreshing = false
                            }
                            is QueuePassengerState.FailedListQueueSkipped -> {
                                setListQueue(binding.tabLayout.selectedTabPosition)
                                binding.showData = true
                                binding.successListQueue = false
                            }
                            QueuePassengerState.SuccessListQueueSkipped -> {
                                setListQueue(binding.tabLayout.selectedTabPosition)
                                binding.showData = true
                            }
                            QueuePassengerState.ProsesSkipQueue -> {
                                val bundle = Bundle()
                                bundle.putLong("queue_id", currentQueueCache.id)
                                bundle.putString("number", currentQueueCache.number)
                                bundle.putLong("location_id", mUserInfo.locationId)
                                bundle.putLong("sub_location_id", mUserInfo.subLocationId)

                                val dialogSkipQueue = FragmentDialogSkipQueue()
                                dialogSkipQueue.arguments = bundle
                                dialogSkipQueue.show(
                                    requireActivity().supportFragmentManager,
                                    FragmentDialogSkipQueue.TAG
                                )
                            }
                            is QueuePassengerState.ProsesDeleteQueueSkipped -> {
                                val currentData = it.queueReceiptCache
                                FragmentDialogDeleteSkipped(
                                    number = currentData.queueNumber,
                                    queueId = currentData.queueId,
                                    locationId = mUserInfo.locationId,
                                    subLocationId = mUserInfo.subLocationId
                                ).show(
                                    requireActivity().supportFragmentManager,
                                    FragmentDialogSkipQueue.TAG
                                )
                            }
                            is QueuePassengerState.ProsesRestoreQueueSkipped -> {
                                val currentData = it.queueReceiptCache
                                FragmentDialogRestoreSkipped(
                                    number = currentData.queueNumber,
                                    queueId = currentData.queueId,
                                    locationId = mUserInfo.locationId,
                                    subLocationId = mUserInfo.subLocationId
                                ).show(
                                    requireActivity().supportFragmentManager,
                                    FragmentDialogRestoreSkipped.TAG
                                )
                            }
                            is QueuePassengerState.SearchQueue -> {
                                val bundle = Bundle()
                                bundle.putLong("locationId", it.locationId)
                                bundle.putLong("subLocationId", it.subLocationId)
                                bundle.putInt("type", positionType)
                                findNavController().navigate(R.id.searchFleetFragment, bundle)
                            }
                            else -> {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }

        _queuePassengerViewModel.waitingQueueCount.observe(viewLifecycleOwner) {
            binding.tabLayout.getTabAt(0)?.text = requireContext().getString(R.string.tab_waiting_format, it)
        }

        _queuePassengerViewModel.skippedQueueCount.observe(viewLifecycleOwner) {
            binding.tabLayout.getTabAt(1)?.text = requireContext().getString(R.string.tab_delayed_format, it)
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
    }

    private fun setupWaiting() {
        val countWaiting = _queuePassengerViewModel.listQueueWaitingCache.count
        val adapter = AdapterCustom(_queuePassengerViewModel.listQueueWaitingCache.queue)
        binding.recyclerView.adapter = adapter

        if (countWaiting > 0L) {
            binding.successListQueue = true
        }
    }

    fun setListQueue(position: Int) {
        positionType = position
        binding.successListQueue = false
        if (position == 0) {
            setupWaiting()
        } else if (position == 1) {
            setupSkipped()
        }
    }

    private fun setupSkipped() {
        val adapter = AdapterCustomSkipped(
            _queuePassengerViewModel.listQueueSkippedCache.queue,
            _queuePassengerViewModel
        )
        binding.recyclerView.adapter = adapter

        val countSkipped = _queuePassengerViewModel.listQueueSkippedCache.count
        binding.successListQueue = countSkipped > 0
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            _queuePassengerViewModel.init()
            binding.swipeRefreshLayout.isRefreshing = false
        }
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