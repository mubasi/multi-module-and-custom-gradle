package id.bluebird.vsm.feature.queue_fleet.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
            showList = false
        }
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
                                mBinding.showList = true
                                _fleetAdapter.submitData(it.list)
                            }
                            is QueueFleetState.FleetDeparted -> {
                                _fleetAdapter.submitData(it.list)
                                _fleetAdapter.notifyItemRemoved(it.removedIndex)
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
                            is QueueFleetState.AddFleetSuccess -> {
                                _fleetAdapter.submitData(it.list)
                                _fleetAdapter.notifyItemInserted(_fleetAdapter.itemCount)
                            }
                            is QueueFleetState.FailedGetList -> {
                                mBinding.showList = true
                            }
                            QueueFleetState.ProgressGetUser -> {
                                mBinding.showList = false
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
                                    childFragmentManager.findFragmentByTag(FragmentRitaseRecordDialog.TAG)
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
                                navigateToSearchQueue(it.fleet, it.locationId, it.subLocationId, it.currentQueueId)
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
        findNavController().navigate(destination)
        setFragmentResultListener(FragmentAddFleet.REQUEST_SELECT) { _, bundle ->
            _mQueueFleetViewModel.showRecordRitase(
                fleetItem,
                bundle.getString(FragmentAddFleet.RESULT_SELECT) ?: ""
            )
        }
    }

    private fun navigationToAddFleet(subLocationId: Long) {
        val destination = FragmentQueueFleetDirections.actionQueueFleetFragmentToAddFleetFragment()
            .apply {
                subLocation = subLocationId
            }
        findNavController().navigate(destination)
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
}