package id.bluebird.vsm.feature.queue_fleet.search_fleet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.adapter.FleetsAdapter
import id.bluebird.vsm.feature.queue_fleet.databinding.SearchFleetFragmentBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFleetFragment : Fragment() {

    companion object {
        const val REQUEST_SEARCH = "requestSearch"
        const val RESULT_SEARCH = "resultSearch"
    }

    private val _fleetAdapter: FleetsAdapter by inject()
    private lateinit var _mBinding: SearchFleetFragmentBinding
    private val _searchFleetViewModel: SearchFleetViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding =
            DataBindingUtil.inflate(inflater, R.layout.search_fleet_fragment, container, false)
        return _mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_mBinding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vm = _searchFleetViewModel
        }
        initRcv()
        _searchFleetViewModel.init(_fleetAdapter.currentList)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _searchFleetViewModel.searchState.collect {
                    when (it) {
                        is SearchFleetState.SuccessDepartFleet -> {
                            val bundle = Bundle()
                            setFragmentResult(RESULT_SEARCH, bundle)
                            findNavController().popBackStack()
                        }
                        is SearchFleetState.UpdateFleetItems -> {
                            bindListView(true)
                            _fleetAdapter.submitList(it.list)
                        }
                        is SearchFleetState.RequestDepartFleetItem -> {
                            val bundle = Bundle().apply {
                                putParcelable(REQUEST_SEARCH, it.fleetItem)
                            }
                            setFragmentResult(RESULT_SEARCH, bundle)
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun bindListView(visibleList: Boolean) {
        with(_mBinding) {
            rcvSearchFleet.isVisible = visibleList
            pbListSearchFleet.isVisible = visibleList.not()
        }
    }

    private fun initRcv() {
        with(_mBinding) {
            _fleetAdapter.initViewModel(_searchFleetViewModel)
            rcvSearchFleet.adapter = _fleetAdapter
            rcvSearchFleet.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}