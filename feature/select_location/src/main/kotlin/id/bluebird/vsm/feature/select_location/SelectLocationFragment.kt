package id.bluebird.vsm.feature.select_location

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
import id.bluebird.vsm.feature.select_location.adapter.AdapterSelectLocation
import id.bluebird.vsm.feature.select_location.databinding.SelectLocationFragmentBinding
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationFragment : Fragment() {

    private val _vm: SelectLocationViewModel by viewModel()
    private lateinit var _mBinding: SelectLocationFragmentBinding
    private val _adapterSelectLocation: AdapterSelectLocation by lazy {
        AdapterSelectLocation(_vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.select_location_fragment,
            container,
            false
        )
        return _mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_mBinding) {
            this.lifecycleOwner = viewLifecycleOwner
        }
        initRcv()
        initRefreshLayout()
        launch()
        _vm.init(arguments?.getBoolean("isMenu", false) ?: false)
    }

    private fun launch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _vm.state.collect {
                    when (it) {
                        is SelectLocationState.GetLocationSuccess -> {
                            _adapterSelectLocation.submitList(it.locationModes)
                            _mBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        is SelectLocationState.OnItemClick -> {
                            _adapterSelectLocation.expandOrCollapseParent(it.locationModel.id)
                        }
                        is SelectLocationState.OnError -> {
                            _mBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        is SelectLocationState.ToAssign -> {
                            NavigationNav.navigate(
                                if (it.isFleetMenu) {
                                    NavigationSealed.QueueFleet(frag = this@SelectLocationFragment)
                                } else {
                                    NavigationSealed.QueuePassenger(frag = this@SelectLocationFragment)
                                }
                            )
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }

    private fun initRcv() {
        with(_mBinding) {
            rcvSelectLocationFragment.layoutManager = LinearLayoutManager(requireContext())
            rcvSelectLocationFragment.adapter = _adapterSelectLocation
        }
    }

    private fun initRefreshLayout() {
        _mBinding.swipeRefreshLayout.setOnRefreshListener {
            _vm.init(arguments?.getBoolean("isMenu", false) ?: false)
        }
    }
}