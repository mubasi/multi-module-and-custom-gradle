package id.bluebird.vsm.feature.queue_fleet.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.*
import androidx.core.text.bold
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.adapter.AdapterFleets
import id.bluebird.vsm.feature.queue_fleet.add_fleet.FragmentAddFleet
import id.bluebird.vsm.feature.queue_fleet.databinding.FleetFragmentBinding
import id.bluebird.vsm.feature.queue_fleet.depart_fleet.FragmentDepartFleetDialog
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import id.bluebird.vsm.feature.queue_fleet.request_fleet.FragmentRequestFleetDialog
import id.bluebird.vsm.feature.queue_fleet.ritase_record.FragmentRitaseRecordDialog
import id.bluebird.vsm.feature.queue_fleet.search_fleet.FragmentSearchFleet
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentQueueFleet : Fragment() {

    companion object {
        const val POSITION = 0
    }

    private val _mQueueFleetViewModel: QueueFleetViewModel by viewModel()
    private lateinit var mBinding: FleetFragmentBinding
    private val _fleetAdapter: AdapterFleets by inject()
    private val _args by navArgs<FragmentQueueFleetArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fleet_fragment,
            container,
            false
        )
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mQueueFleetViewModel.stateIdle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            vm = _mQueueFleetViewModel
            lifecycleOwner = viewLifecycleOwner
            showProgress = true
            successList = false
        }
        setHasOptionsMenu(true)
        initRcv()
        observer()
        _mQueueFleetViewModel.init()

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(_mQueueFleetViewModel) {
                    queueFleetState.collectLatest {
                        when (it) {
                            QueueFleetState.ProgressHolder -> {
                                mBinding.showHolder = true
                            }
                            QueueFleetState.ToSelectLocation -> {
                                mBinding.showHolder = false
                                NavigationNav.navigate(
                                    NavigationSealed.SelectLocation(
                                        destination = null,
                                        frag = this@FragmentQueueFleet,
                                        isMenuFleet = true
                                    )
                                )
                            }
                            is QueueFleetState.AddFleet -> {
                                navigationToAddFleet(it.subLocationId)
                            }
                            is QueueFleetState.SearchFleet -> {
                                navigateToSearchFleet()
                            }
                            QueueFleetState.GetUserInfoSuccess -> {
                                initLocation(_args.locationId, _args.subLocationId)
                                getCounter()
                                getFleetList()
                            }
                            is QueueFleetState.GetListSuccess -> {
                                mBinding.showProgress = false
                                mBinding.successList = true
                                _fleetAdapter.submitData(it.list)
                            }
                            is QueueFleetState.NotifyDataFleetChanged -> {
                                _fleetAdapter.submitData(it.list)
                                mBinding.successList = it.list.isNotEmpty()
                            }
                            is QueueFleetState.GetListEmpty -> {
                                _fleetAdapter.submitData(arrayListOf())
                            }
                            is QueueFleetState.ShowRequestFleet -> {
                                FragmentRequestFleetDialog(
                                    it.subLocationId,
                                    _mQueueFleetViewModel::updateRequestCount
                                ).show(
                                    childFragmentManager,
                                    FragmentRequestFleetDialog.TAG
                                )
                            }
                            is QueueFleetState.FailedGetList -> {
                                mBinding.showProgress = false
                                mBinding.successList = false
                            }
                            QueueFleetState.ProgressGetUser -> {
                                mBinding.showProgress = false
                            }
                            is QueueFleetState.RequestDepartFleet -> {
                                FragmentDepartFleetDialog(
                                    it.fleet,
                                    it.locationId,
                                    it.subLocationId,
                                    ::departFleet,
                                    ::onErrorFromDialog
                                )
                                    .show(
                                        childFragmentManager,
                                        FragmentDepartFleetDialog.TAG
                                    )
                            }
                            is QueueFleetState.RecordRitaseToDepart -> {
                                val fragment =
                                    childFragmentManager.findFragmentByTag(
                                        FragmentRitaseRecordDialog.TAG
                                    )
                                if (fragment is FragmentRitaseRecordDialog) {
                                    fragment.updateQueue(it.queueId)
                                } else {
                                    FragmentRitaseRecordDialog(
                                        it.fleet,
                                        it.queueId,
                                        it.locationId,
                                        it.subLocationId,
                                        ::departFleet,
                                        ::showSearchQueue
                                    )
                                        .show(
                                            childFragmentManager,
                                            FragmentRitaseRecordDialog.TAG
                                        )
                                }
                            }
                            is QueueFleetState.SearchQueueToDepart -> {
                                navigateToSearchQueue(
                                    it.fleet,
                                    it.locationId,
                                    it.subLocationId,
                                    it.currentQueueId
                                )
                            }
                            is QueueFleetState.SuccessDepartFleet -> {
                                successDialogDepartFleet(it.fleetNumber, it.isWithPassenger)
                            }
                            is QueueFleetState.FailedDepart -> {
                                val string = SpannableStringBuilder()
                                    .bold { append(getString(R.string.fleet)) }
                                    .append(" ")
                                    .append(getString(R.string.failed_depart_description))

                                showSnackbar(string, R.color.warning_0)
                            }
                            is QueueFleetState.FailedGetQueue -> {
                                val string = SpannableStringBuilder()
                                    .append(it.throwable.message ?: "Gagal mendapatkan Antrian")

                                showSnackbar(string, R.color.warning_0)
                            }
                            is QueueFleetState.GoToQrCodeScreen -> {
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
    }

    private fun successDialogDepartFleet(fleetNumber: String, isWithPassenger: Boolean) {
        val string = SpannableStringBuilder()
            .bold { append(fleetNumber) }
            .append(" ")
            .append(
                if (isWithPassenger) getString(R.string.fleet_depart_with_passenger)
                else getString(R.string.fleet_depart_without_passenger)
            )
        showSnackbar(string, R.color.success_0)
        _mQueueFleetViewModel.removeFleet(fleetNumber)
    }

    private fun showSnackbar(message: Spanned, color: Int) {
        DialogUtils.showSnackbar(requireView(), message, color)
    }

    private fun navigateToSearchFleet() {
        findNavController().navigate(R.id.action_queueFleetFragment_to_searchFragment)
        setFragmentResultListener(FragmentSearchFleet.RESULT_SEARCH) { _, bundle ->
            val fleet = bundle.getParcelable<FleetItem>(FragmentSearchFleet.REQUEST_SEARCH)
                ?: return@setFragmentResultListener
            _mQueueFleetViewModel.requestDepart(fleet)
        }
    }

    private fun navigateToSearchQueue(
        fleetItem: FleetItem,
        locationId: Long,
        subLocationId: Long,
        currentQueueId: String,
    ) {
        val destination =
            FragmentQueueFleetDirections.actionQueueFleetFragmentToSearchQueueFragment()
                .apply {
                    this.location = locationId
                    this.subLocation = subLocationId
                    this.currentQueue = currentQueueId
                }

        setFragmentResultListener(FragmentAddFleet.REQUEST_SELECT) { _, bundle ->
            _mQueueFleetViewModel.showRecordRitase(
                fleetItem,
                bundle.getString(FragmentAddFleet.RESULT_SELECT) ?: ""
            )
        }
    }

    private fun navigationToAddFleet(subLocationId: Long) {
        with(findNavController()) {
            if (currentDestination?.id == R.id.queueFleetFragment) {
                val destination =
                    FragmentQueueFleetDirections.actionQueueFleetFragmentToAddFleetFragment()
                        .apply {
                            subLocation = subLocationId
                        }
                navigate(destination)
            }
        }
        setFragmentResultListener(FragmentAddFleet.RESULT) { _, bundle ->
            _mQueueFleetViewModel.addSuccess(bundle.getParcelable(FragmentAddFleet.REQUEST_ADD))
        }
    }

    private fun initRcv() {
        with(mBinding) {
            _fleetAdapter.initViewModel(_mQueueFleetViewModel)
            mainListFleetFleetFragment.adapter = _fleetAdapter
            mainListFleetFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.queue_fleet_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.qr_code_screen -> {
                _mQueueFleetViewModel.goToQrCodeScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun gotoQrcodeScreen(locationId: Long, subLocationId: Long, titleLocation: String) {
        NavigationNav.navigate(
            NavigationSealed.QrCode(
                destination = null,
                frag = this@FragmentQueueFleet,
                locationId = locationId,
                subLocationId = subLocationId,
                titleLocation = titleLocation,
                position = POSITION.toLong()
            )
        )
    }
}