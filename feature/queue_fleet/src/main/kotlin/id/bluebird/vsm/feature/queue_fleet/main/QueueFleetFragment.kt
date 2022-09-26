package id.bluebird.vsm.feature.queue_fleet.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.view.isVisible
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
import id.bluebird.vsm.feature.queue_fleet.adapter.FleetsAdapter
import id.bluebird.vsm.feature.queue_fleet.add_fleet.AddFleetFragment
import id.bluebird.vsm.feature.queue_fleet.databinding.FleetFragmentBinding
import id.bluebird.vsm.feature.queue_fleet.depart_fleet.DepartFleetDialog
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import id.bluebird.vsm.feature.queue_fleet.request_fleet.RequestFleetDialog
import id.bluebird.vsm.feature.queue_fleet.ritase_record.RitaseRecordDialog
import id.bluebird.vsm.feature.queue_fleet.search_fleet.SearchFleetFragment
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFleetFragment : Fragment() {

    private val _mQueueFleetViewModel: QueueFleetViewModel by viewModel()
    private lateinit var mBinding: FleetFragmentBinding
    private val _fleetAdapter: FleetsAdapter by inject()
    private val _args by navArgs<QueueFleetFragmentArgs>()

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
            showList = false
        }
        initRcv()
        observer(view)
        _mQueueFleetViewModel.init()

    }

    private fun observer(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_mQueueFleetViewModel) {
                    queueFleetState.collect {
                        when (it) {
                            QueueFleetState.ProgressHolder -> {
                                mBinding.showHolder = true
                            }
                            QueueFleetState.ToSelectLocation -> {
                                mBinding.showHolder = false
                                NavigationNav.navigate(
                                    NavigationSealed.SelectLocation(
                                        destination = null,
                                        frag = this@QueueFleetFragment,
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
                                mBinding.showList = true
                                _fleetAdapter.submitList(it.list)
                            }
                            is QueueFleetState.FleetDeparted -> {
                                _fleetAdapter.submitList(it.list)
                                _fleetAdapter.notifyItemRemoved(it.removedIndex)
                            }
                            is QueueFleetState.GetListEmpty -> {
                                _fleetAdapter.submitList(arrayListOf())
                            }
                            is QueueFleetState.ShowRequestFleet -> {
                                RequestFleetDialog(
                                    it.subLocationId,
                                    _mQueueFleetViewModel::updateRequestCount
                                ).show(
                                    childFragmentManager,
                                    RequestFleetDialog.TAG
                                )
                            }
                            is QueueFleetState.AddFleetSuccess -> {
                                _fleetAdapter.submitList(it.list)
                                _fleetAdapter.notifyItemInserted(_fleetAdapter.itemCount)
                            }
                            is QueueFleetState.FailedGetList -> {
                                mBinding.showList = true
                            }
                            QueueFleetState.ProgressGetUser -> {
                                mBinding.showList = false
                            }
                            is QueueFleetState.RequestDepartFleet -> {
                                DepartFleetDialog(
                                    it.fleet,
                                    ::departFleet
                                )
                                    .show(
                                        childFragmentManager,
                                        DepartFleetDialog.TAG
                                    )
                            }
                            is QueueFleetState.RecordRitaseToDepart -> {
                                val fragment =
                                    childFragmentManager.findFragmentByTag(RitaseRecordDialog.TAG)
                                if (fragment is RitaseRecordDialog) {
                                    fragment.updateQueue(it.queueId)
                                } else {
                                    RitaseRecordDialog(
                                        it.fleet,
                                        it.queueId,
                                        it.subLocationId,
                                        ::departFleet,
                                        ::showSearchQueue
                                    )
                                        .show(
                                            childFragmentManager,
                                            RitaseRecordDialog.TAG
                                        )
                                }
                            }
                            is QueueFleetState.SearchQueueToDepart -> {
                                navigateToSearchQueue(it.fleet, it.subLocationId, it.currentQueueId)
                            }
                            is QueueFleetState.SuccessDepartFleet -> {
                                successDialogDepartFleet(it.fleetNumber, it.isWithPassenger)
                            }
                            is QueueFleetState.FailedDepart -> {
                                val string = SpannableStringBuilder()
                                    .bold { append(getString(R.string.fleet)) }
                                    .append(" ")
                                    .append(getString(R.string.failed_depart_description))

                                DialogUtils.showSnackbar(view, string, R.color.warning_0)
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
        DialogUtils.showSnackbar(requireView(), string, R.color.success_0)

        _mQueueFleetViewModel.removeFleet(fleetNumber)
    }

    private fun navigateToSearchFleet() {
        findNavController().navigate(R.id.action_queueFleetFragment_to_searchFragment)
        setFragmentResultListener(SearchFleetFragment.RESULT_SEARCH) { _, bundle ->
            val fleet = bundle.getParcelable<FleetItem>(SearchFleetFragment.REQUEST_SEARCH)
                ?: return@setFragmentResultListener
            _mQueueFleetViewModel.requestDepart(fleet)
        }
    }

    private fun navigateToSearchQueue(
        fleetItem: FleetItem,
        subLocationId: Long,
        currentQueueId: String,
    ) {
        val destination =
            QueueFleetFragmentDirections.actionQueueFleetFragmentToSearchQueueFragment()
                .apply {
                    this.subLocation = subLocationId
                    this.currentQueue = currentQueueId
                }
        findNavController().navigate(destination)
        setFragmentResultListener(AddFleetFragment.REQUEST_SELECT) { _, bundle ->
            _mQueueFleetViewModel.showRecordRitase(
                fleetItem,
                bundle.getString(AddFleetFragment.RESULT_SELECT)
            )
        }
    }

    private fun navigationToAddFleet(subLocationId: Long) {
        val destination = QueueFleetFragmentDirections.actionQueueFleetFragmentToAddFleetFragment()
            .apply {
                subLocation = subLocationId
            }
        findNavController().navigate(destination)
        setFragmentResultListener(AddFleetFragment.RESULT) { _, bundle ->
            _mQueueFleetViewModel.addSuccess(bundle.getParcelable(AddFleetFragment.REQUEST_ADD))
        }
    }

    private fun initRcv() {
        with(mBinding) {
            _fleetAdapter.initViewModel(_mQueueFleetViewModel)
            mainListFleetFleetFragment.adapter = _fleetAdapter
            mainListFleetFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}