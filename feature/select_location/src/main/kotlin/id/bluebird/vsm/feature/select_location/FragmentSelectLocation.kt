package id.bluebird.vsm.feature.select_location

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.select_location.adapter.AdapterSelectLocation
import id.bluebird.vsm.feature.select_location.databinding.SelectLocationFragmentBinding
import id.bluebird.vsm.feature.select_location.model.CacheParentModel
import id.bluebird.vsm.feature.select_location.model.SubLocation
import id.bluebird.vsm.feature.select_location.search_mall_location.FragmentSearchMallLocation
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSelectLocation : Fragment() {

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
        setHasOptionsMenu(true)
        initRcv()
        initRefreshLayout()
        launch()
        onFragmentListenerResult()
        _vm.init(arguments?.getBoolean("isMenu", false) ?: false)
    }

    private fun launch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _vm.state.collect {
                    when (it) {
                        is SelectLocationState.OnProgressGetLocations -> {
                            setupVisibility(progress = true, data  = false)
                        }
                        is SelectLocationState.GetLocationSuccess -> {
                            _adapterSelectLocation.submitList(it.locationModes)
                            setupVisibility(progress = false, data  = true)
                        }
                        is SelectLocationState.OnItemClick -> {
                            _adapterSelectLocation.expandOrCollapseParent(it.locationModel.id)
                        }
                        is SelectLocationState.OnError -> {
                            setupVisibility(progress = false, data  = true)
                        }
                        is SelectLocationState.ToAssign -> {
                            NavigationNav.navigate(
                                if (it.isFleetMenu) {
                                    NavigationSealed.QueueFleet(frag = this@FragmentSelectLocation)
                                } else {
                                    NavigationSealed.QueuePassenger(frag = this@FragmentSelectLocation)
                                }
                            )
                        }
                        is SelectLocationState.EmptyLocation -> {
                            val title = requireContext().getString(R.string.title_not_show_location)
                            val msg = requireContext().getString(R.string.msg_not_show_location)
                            DialogUtils.showErrorDialog(requireContext(), title, msg)
                        }
                        is SelectLocationState.SearchLocation -> {
                            val listParent : ArrayList<CacheParentModel> = ArrayList()
                            val listChild : ArrayList<SubLocation> = ArrayList()
                            _vm._locations.forEach { result ->
                                val tempParent = CacheParentModel(
                                    id = result.id,
                                    name = result.name,
                                    isExpanded = result.isExpanded,
                                    type = result.type
                                )
                                listParent.add(tempParent)
                                listChild.addAll(result.list)
                            }
                            val bundle = Bundle()
                            bundle.putParcelableArray("parentList", listParent.toTypedArray())
                            bundle.putParcelableArray("childList", listChild.toTypedArray())
                            findNavController().navigate(R.id.searchLocationFragment, bundle)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_location_action, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.searchScreen -> {
                _vm.searchScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupVisibility(progress : Boolean, data : Boolean) {
        _mBinding.swipeRefreshLayout.isRefreshing = progress
        _mBinding.progressPage.isVisible = progress
        _mBinding.tvTitle.isVisible = data
    }

    private fun onFragmentListenerResult() {
        setFragmentResultListener(FragmentSearchMallLocation.NOTIFICATION_MESSAGE ) { key, bundle ->
            val locationId = bundle.getLong("locationId")
            val subLocationId = bundle.getLong("subLocationId")
            if(locationId != null && subLocationId != null) {
                _vm.setFromSearch(locationId, subLocationId)
            }
        }
    }
}