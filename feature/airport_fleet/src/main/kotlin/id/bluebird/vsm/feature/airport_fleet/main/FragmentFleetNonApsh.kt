package id.bluebird.vsm.feature.airport_fleet.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.core.utils.StringUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.assign_location.FragmentAssignLocation
import id.bluebird.vsm.feature.airport_fleet.databinding.FragmentFleetNonApshBinding
import id.bluebird.vsm.feature.airport_fleet.dialog_request_stock.FragmentDialogRequestStock
import id.bluebird.vsm.feature.airport_fleet.main.FleetNonApshViewModel.Companion.EMPTY_STRING
import id.bluebird.vsm.feature.airport_fleet.main.adapter.AdapterFleetList
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFleetNonApsh : Fragment() {

    private val vm: FleetNonApshViewModel by viewModel()
    private lateinit var binding: FragmentFleetNonApshBinding
    private var bottomDialogProgress: BottomSheetDialog? = null
    private val adapterFleetNonApsh: AdapterFleetList by lazy {
        AdapterFleetList(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_fleet_non_apsh,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding()
        setupSwipeRefresh()
        setupAdapter()
        setLiveDataObserve()
        checkStatusAssignLocation()
        setObserve()
        setArgument()
    }

    override fun onResume() {
        super.onResume()
        vm.clearCacheSelected()
    }

    private fun mBinding() {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
            stProgress = true
        }
    }

    private fun setArgument() {
        vm.initialize()
    }

    private fun setFragmentLabel(label: String) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = label
    }

    private fun setObserve() {
        vm.title.observe(viewLifecycleOwner) {
            setFragmentLabel(
                it
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(vm) {
                    state.collectLatest {
                        when (it) {
                            FleetNonApshState.ToSelectLocation -> {
                                setShowHideProgress(status = true)
                                NavigationNav.navigate(
                                    NavigationSealed.SelectLocation(
                                        destination = null,
                                        frag = this@FragmentFleetNonApsh,
                                        isMenuFleet = true
                                    )
                                )
                            }
                            FleetNonApshState.ProgressHolder -> {
                                setShowHideProgress(status = true)
                            }
                            FleetNonApshState.OnProgress -> {
                                setShowHideProgress(status = true)
                            }
                            is FleetNonApshState.OnEmptyData -> {
                                setShowHideProgress(status = false)
                                statusConditionEmpty(true)
                            }
                            FleetNonApshState.GetCountSuccess -> {
                                setShowHideProgress(status = true)
                                getFleetByLocation()
                            }
                            FleetNonApshState.Idle -> {
                                setShowHideProgress(status = false)
                            }
                            is FleetNonApshState.DialogRequest -> {
                                checkProgressDialog()
                                showProgressDialog(null)
                                delay(500)
                                bottomDialogProgress?.dismiss()
                                FragmentDialogRequestStock(
                                    subLocationId = it.subLocationId,
                                    requestToId = it.requestToId,
                                ) { result ->
                                    if (result > 0) {
                                        updateRequestCount(result)
                                        val message = SpannableStringBuilder()
                                            .append("${title.value} ${getString(R.string.total_request_on_terminal)} $result ")
                                        showTopSnackbar(message,  R.color.success_color, null)
                                    }
                                }.show(
                                    childFragmentManager,
                                    FragmentDialogRequestStock.TAG
                                )
                            }
                            is FleetNonApshState.IntentToAddFleet -> {
                                val bundle = Bundle()
                                bundle.putBoolean("isPerimeter", it.isPerimeter)
                                bundle.putBoolean("isWing", it.isWing)
                                bundle.putLong("subLocationId", it.subLocationId)
                                findNavController().navigate(R.id.actionAddFleetAirport, bundle)
                            }
                            is FleetNonApshState.DispatchCar -> {
                                DialogUtils.showLeavingDialog(
                                    requireContext(),
                                    it.status
                                ) { result ->
                                    vm.dispatchSend(result)
                                }
                            }
                            is FleetNonApshState.SuccessDispatchFleet -> {
                                vm.setConditionListFleet()
                                val msg: String = StringUtils.getMessageRitase(
                                    requireContext(),
                                    it.message,
                                    it.isWithPassenger,
                                    it.isNonTerminal,
                                )
                                val message = SpannableStringBuilder()
                                    .append(msg)
                                showTopSnackbar(message, null, null)
                            }
                            is FleetNonApshState.SuccessArrived -> {
                                val msg = StringUtils.getMessageRitase(
                                    requireContext(),
                                    it.message,
                                    it.isWithPassenger,
                                    it.isStatusArrived,
                                )
                                val message = SpannableStringBuilder()
                                    .append(msg)
                                showTopSnackbar(message, null, null)
                            }
                            is FleetNonApshState.SendCar -> {
                                val bundle = Bundle()
                                bundle.putParcelableArray("fleetList", it.result.toTypedArray())
                                bundle.putBoolean("isPerimeter", isPerimeter.value ?: false)
                                findNavController().navigate(R.id.action_assign_location, bundle)
                            }
                            is FleetNonApshState.TakePicture -> {
                                val bundle = Bundle()
                                bundle.putLong("subLocationId", it.subLocationId)
                                findNavController().navigate(R.id.take_picture_airport, bundle)
                            }
                            is FleetNonApshState.OnError -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    getString(R.string.title_something_wrong),
                                    it.err.message.toString()
                                )
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

    private fun setLiveDataObserve() {
        with(vm) {
            fleetLiveData.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    setEmptyList()
                }
                statusConditionEmpty(it.isEmpty())
                adapterFleetNonApsh.updateData(it)
            }
            searchFleetInfo.observe(viewLifecycleOwner) {
                statusConditionEmpty(false)
                if (it.isEmpty()) {
                    clearSearch()
                }
                setFilterFleet(it)
            }
            selectFleetCounter.observe(viewLifecycleOwner) {
                adapterFleetNonApsh.updateCount(it)
            }
            updateButtonFleetItem.observe(viewLifecycleOwner) {
                adapterFleetNonApsh.notifyDataSetChanged()
            }
        }
    }

    private fun setFilterFleet(params: String) {
        if (filterFleet(params).isEmpty()) {
            vm.updateListFleetState(true)
            statusConditionEmpty(true)
        } else {
            adapterFleetNonApsh.filter.filter(params)
        }
    }

    private fun setShowHideProgress(status: Boolean) {
        binding.stProgress = status
    }

    private fun setupSwipeRefresh() {
        binding.refreshPage.setOnRefreshListener {
            setArgument()
            clearSearch()
            statusConditionEmpty(false)
            vm.clearCacheSelected()
            binding.refreshPage.isRefreshing = false
        }
    }

    private fun clearSearch() {
        vm.carSearch.value = EMPTY_STRING
        binding.fieldSearch.text?.clear()
    }

    private fun setupAdapter() {
        binding.fleetListRcv.layoutManager = LinearLayoutManager(requireContext())
        binding.fleetListRcv.adapter = adapterFleetNonApsh
    }

    private fun filterFleet(params: String): ArrayList<AssignmentCarCache> {
        val filtered: ArrayList<AssignmentCarCache> = ArrayList()
        for (item in vm.fleetLiveData.value!!) {
            if (item.fleetNumber.toLowerCase().contains(params.toLowerCase())) {
                filtered.add(item)
            }
        }
        return filtered
    }

    private fun statusConditionEmpty(show: Boolean) {
        binding.statusConditionEmpty.isVisible = show
    }

    private fun checkStatusAssignLocation() {
        setFragmentResultListener(FragmentAssignLocation.NOTIFICATION_MESSAGE) { _, bundle ->
            val status = bundle.getString(FragmentAssignLocation.MESSAGE)
            if (!status.isNullOrEmpty() && status != FragmentAssignLocation.BACK) {
                vm.updateStockFromList(FleetNonApshViewModel.StatusUpdate.DEFICIENT)
                vm.removeUpdateData()
            } else {
                vm.clearCacheSelected()
            }
        }
    }

    private fun showTopSnackbar(msg: Spanned, backgroundColor: Int?, textColor: Int?) {
        DialogUtils.showSnackbar(requireView(), requireContext(), msg, backgroundColor, textColor)
    }

    private fun checkProgressDialog() {
        if (bottomDialogProgress?.isShowing == true) {
            bottomDialogProgress?.dismiss()
        }
    }

    private fun showProgressDialog(message: String?) {
        bottomDialogProgress = DialogUtils.progressDialog(requireContext(), message)
    }

}