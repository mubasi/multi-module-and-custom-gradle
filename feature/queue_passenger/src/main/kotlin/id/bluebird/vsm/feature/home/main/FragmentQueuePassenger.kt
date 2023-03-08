package id.bluebird.vsm.feature.home.main

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.FragmentQueuePassengerBinding
import id.bluebird.vsm.feature.home.dialog_delete_skipped.FragmentDialogDeleteSkipped
import id.bluebird.vsm.feature.home.dialog_queue_receipt.FragmentDialogQueueReceipt
import id.bluebird.vsm.feature.home.dialog_record_ritase.FragmentDialogRecordRitase
import id.bluebird.vsm.feature.home.dialog_restore_skipped.FragmentDialogRestoreSkipped
import id.bluebird.vsm.feature.home.dialog_skip_queue.FragmentDialogSkipQueue
import id.bluebird.vsm.feature.home.main.QueuePassengerViewModel.Companion.EMPTY_STRING
import id.bluebird.vsm.feature.home.ritase_fleet.FragmentRitaseFleet
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentQueuePassenger : Fragment() {

    companion object {
        const val POSITION = 1L
    }

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
        setHasOptionsMenu(true)
        binding.showData = false
        binding.successCurrentQueue = false
        binding.successListQueue = false

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        setupTabLayout()
        setupListQueue()
        setupSwipeRefresh()
        observer()
        checkNavigationData()
        _queuePassengerViewModel.init()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.queue_passenger_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.qr_code_screen -> {
                _queuePassengerViewModel.toQrCodeScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                                getCounterAndListQueue()
                            }
                            QueuePassengerState.SuccessCurrentQueue -> {
                                binding.successCurrentQueue = true
                                setupFirst()
                            }
                            is QueuePassengerState.FailedCurrentQueue -> {
                                binding.successCurrentQueue = false
                                setupFirst()
                            }
                            QueuePassengerState.ProsesSkipQueue -> {
                                val bundle = Bundle()
                                bundle.putLong("queue_id", currentQueueCache.id)
                                bundle.putString("number", currentQueueCache.number)
                                bundle.putLong("location_id", mUserInfo.locationId)
                                bundle.putLong("sub_location_id", mUserInfo.subLocationId)

                                val dialogSkipQueue = FragmentDialogSkipQueue {
                                    getCounterAndListQueue()
                                }
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
                            is QueuePassengerState.ToSearchQueue -> {
                                val bundle = Bundle()
                                bundle.putLong("locationId", it.locationId)
                                bundle.putLong("subLocationId", it.subLocationId)
                                bundle.putInt("type", positionType)
                                findNavController().navigate(R.id.searchFleetFragment, bundle)
                            }
                            is QueuePassengerState.ProsesRitase -> {
                                showDialogRecordRitase(EMPTY_STRING)
                            }
                            is QueuePassengerState.ToQrCodeScreen -> {
                                gotoQrcodeScreen(it.locationId, it.subLocationId, it.titleLocation)
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
            binding.tabLayout.getTabAt(0)?.text =
                requireContext().getString(R.string.tab_waiting_format, it)
            setupWaiting()
        }

        _queuePassengerViewModel.skippedQueueCount.observe(viewLifecycleOwner) {
            binding.tabLayout.getTabAt(1)?.text =
                requireContext().getString(R.string.tab_delayed_format, it)
        }
    }

    private fun setupListQueue() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFirst() {
        _queuePassengerViewModel.getListQueue()
        _queuePassengerViewModel.getListQueueSkipped()
        binding.apply {
            showData = true
            successListQueue = false
            swipeRefreshLayout.isRefreshing = false
        }
        val selectedPosition = binding.tabLayout.selectedTabPosition
        setListQueue(selectedPosition)
    }

    private fun setupWaiting() {
        val countWaiting = _queuePassengerViewModel.listQueueWaitingCache.count
        val adapter = AdapterCustom(_queuePassengerViewModel.listQueueWaitingCache.queue)
        binding.apply {
            recyclerView.adapter = adapter
            binding.successListQueue = countWaiting > 0
        }
    }

    fun setListQueue(position: Int) {
        positionType = position
        if (position == 0) {
            setupWaiting()
        } else if (position == 1) {
            setupSkipped()
        }
    }

    private fun setupSkipped() {
        val countSkipped = _queuePassengerViewModel.listQueueSkippedCache.count
        val adapter = AdapterCustomSkipped(
            _queuePassengerViewModel.listQueueSkippedCache.queue,
            _queuePassengerViewModel
        )
        binding.apply {
            recyclerView.adapter = adapter
            successListQueue = countSkipped > 0L
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getCounterAndListQueue()
            binding.showData = false
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

    private fun getCounterAndListQueue() {
        _queuePassengerViewModel.getCurrentQueue()
        _queuePassengerViewModel.getCounterBar()
    }

    private fun checkNavigationData() {
        setFragmentResultListener(FragmentRitaseFleet.RITASE_FLEET) { _, bundle ->
            val status = bundle.getString(FragmentRitaseFleet.FLEET_NUMBER)
            if (!status.isNullOrEmpty()) {
                showDialogRecordRitase(status)
            }
        }
    }

    private fun showDialogRecordRitase(fleetNumber: String) {
        FragmentDialogRecordRitase(
            locationId = _queuePassengerViewModel.mUserInfo.locationId,
            subLocationId = _queuePassengerViewModel.mUserInfo.subLocationId,
            queue = _queuePassengerViewModel.currentQueueCache,
            fleetNumber = fleetNumber,
            userId = _queuePassengerViewModel.mUserInfo.userId
        ) { numberFleet, numberQueue ->
            callBackProses(
                fleetNumber = numberFleet,
                queueNumber = numberQueue
            )
        }.show(
            requireActivity().supportFragmentManager,
            FragmentDialogRecordRitase.TAG
        )
    }

    private fun callBackProses(fleetNumber: String, queueNumber: String) {
        getCounterAndListQueue()
        val message = Html.fromHtml(
            "<b>No. antrian ${queueNumber}</b> berhasil berangkat dengan <b>${fleetNumber}</b>",
            1
        )
        showNotifInfo(message, R.color.success_color)
    }

    private fun showNotifInfo(message: Spanned, color: Int) {
        DialogUtils.showSnackbar(requireView(), message, color)
    }

    private fun gotoQrcodeScreen(locationId: Long, subLocationId: Long, titleLocation: String) {
        NavigationNav.navigate(
            NavigationSealed.QrCode(
                destination = null,
                frag = this@FragmentQueuePassenger,
                locationId = locationId,
                subLocationId = subLocationId,
                titleLocation = titleLocation,
                position = POSITION
            )
        )
    }
}