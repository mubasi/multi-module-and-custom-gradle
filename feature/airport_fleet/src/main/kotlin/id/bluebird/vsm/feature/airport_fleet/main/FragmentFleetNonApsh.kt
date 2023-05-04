package id.bluebird.vsm.feature.airport_fleet.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.FragmentFleetNonApshBinding
import id.bluebird.vsm.feature.airport_fleet.dialog_request_stock.FragmentDialogRequestStock
import id.bluebird.vsm.feature.airport_fleet.main.FleetNonApshViewModel.Companion.EMPTY_STRING
import id.bluebird.vsm.feature.airport_fleet.main.model.FleetItemCar
import id.bluebird.vsm.feature.airport_fleet.main.adapter.AdapterFleetList
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFleetNonApsh : Fragment() {

    private val vm: FleetNonApshViewModel by viewModel()
    private lateinit var binding: FragmentFleetNonApshBinding
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
                it.ifEmpty { getString(R.string.fleet_title) }
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
                                FragmentDialogRequestStock(
                                    subLocationId = it.subLocationId,
                                    subLocationName = it.subLocationName,
                                    requestToId = it.requestToId,
                                    vm::updateRequestCount
                                ).show(
                                    childFragmentManager,
                                    FragmentDialogRequestStock.TAG
                                )
                            }
                            is FleetNonApshState.IntentToAddFleet -> {
                                val bundle = Bundle()
                                bundle.putBoolean("isPerimeter", it.isPerimeter)
                                bundle.putBoolean("isWing", it.isWing)
                                bundle.putLong("subLocationId", it.subLocationId)
//                                findNavController().navigate(R.id.action_add_fleet_non_apsh, bundle)
                            }
                            is FleetNonApshState.DispatchCar -> {
//                                DialogUtils.showLeavingDialog(requireContext(), it.status) { result ->
//                                    vm.dispatchSend(result)
//                                }
                            }
                            is FleetNonApshState.SuccessDispatchFleet -> {
                                vm.setConditionListFleet()
//                                val msg : String = StringUtils.getMessageRitase(
//                                    requireContext(),
//                                    it.message,
//                                    it.isWithPassenger,
//                                    it.isNonTerminal,
//                                )
//                                showTopSnackbar(msg)
                            }
                            is FleetNonApshState.SuccessArrived -> {
//                                val msg = StringUtils.getMessageRitase(
//                                    requireContext(),
//                                    it.message,
//                                    it.isWithPassenger,
//                                    it.isStatusArrived,
//                                )
//                                showTopSnackbar(msg)
                            }
                            is FleetNonApshState.SendCar -> {
                                val bundle = Bundle()
                                bundle.putParcelableArray("fleetList", it.result.toTypedArray())
                                bundle.putBoolean("isPerimeter", isPerimeter)
//                                findNavController().navigate(R.id.action_assign_location, bundle)
                            }
                            is FleetNonApshState.TakePicture -> {
                                val bundle = Bundle()
                                bundle.putLong("subLocationId", it.subLocationId)
//                                findNavController().navigate(R.id.action_take_picture, bundle)
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

    private fun setTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
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

    private fun filterFleet(params: String): ArrayList<FleetItemCar> {
        val filtered: ArrayList<FleetItemCar> = ArrayList()
        for (item in vm.fleetLiveData.value!!) {
            if (item.name.toLowerCase().contains(params.toLowerCase())) {
                filtered.add(item)
            }
        }
        return filtered
    }

    private fun statusConditionEmpty(show: Boolean) {
        binding.statusConditionEmpty.isVisible = show
    }

    private fun checkStatusAssignLocation() {
//        setFragmentResultListener(FragmentAssignLocation.NOTIFICATION_MESSAGE) { _, bundle ->
//            val status = bundle.getString(FragmentAssignLocation.MESSAGE)
//            if (!status.isNullOrEmpty() && status != FragmentAssignLocation.BACK){
//                vm.updateStockFromList(FleetNonApshViewModel.StatusUpdate.DEFICIENT)
//                vm.removeUpdateData()
//            } else {
//                vm.clearCacheSelected()
//            }
//        }
    }

    private fun showTopSnackbar(msg: String) {
//        DialogUtils.topSnackBar(requireView(), requireContext(), msg, R.color.white, null)
    }

}