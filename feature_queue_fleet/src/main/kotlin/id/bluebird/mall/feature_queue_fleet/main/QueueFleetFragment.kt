package id.bluebird.mall.feature_queue_fleet.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.adapter.FleetsAdapter
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetFragment
import id.bluebird.mall.feature_queue_fleet.databinding.FleetFragmentBinding
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialog
import id.bluebird.mall.feature_queue_fleet.search_fleet.SearchFleetFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFleetFragment : Fragment() {

    private val _mQueueFleetViewModel: QueueFleetViewModel by viewModel()
    private lateinit var mBinding: FleetFragmentBinding
    private val _fleetAdapter: FleetsAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        _mQueueFleetViewModel.init()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_mQueueFleetViewModel) {
                    queueFleetState.collect {
                        when (it) {
                            is QueueFleetState.AddFleet -> {
                                navigationToAddFleet(it.subLocationId)
                            }
                            is QueueFleetState.SearchFleet -> {
                                navigateToSearchFleet()
                            }
                            QueueFleetState.GetUserInfoSuccess -> {
                                getCounter()
                                getFleetList()
                            }
                            is QueueFleetState.GetListSuccess -> {
                                mBinding.showList = true
                                _fleetAdapter.submitList(it.list)
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
                            else -> {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToSearchFleet() {
        findNavController().navigate(R.id.action_queueFleetFragment_to_searchFragment)
        setFragmentResultListener(SearchFleetFragment.RESULT_SEARCH) { _, _ ->
            _mQueueFleetViewModel.departSuccess(_fleetAdapter.currentList)
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