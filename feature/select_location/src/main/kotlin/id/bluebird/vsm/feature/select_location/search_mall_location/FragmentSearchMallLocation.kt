package id.bluebird.vsm.feature.select_location.search_mall_location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.select_location.R
import id.bluebird.vsm.feature.select_location.databinding.FragmentSearchLocationBinding
import id.bluebird.vsm.feature.select_location.model.CacheParentModel
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.SubLocation
import id.bluebird.vsm.feature.select_location.search_mall_location.adapter.AdapterSearchMallLocation
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
class FragmentSearchMallLocation : Fragment() {

    companion object {
        const val NOTIFICATION_MESSAGE = "notificationMessage"
        const val EMPTY_STRING = ""
    }

    private val vm : SearchMallLocationViewModel by viewModel()
    private val args : FragmentSearchMallLocationArgs by navArgs()
    private lateinit var binding : FragmentSearchLocationBinding
    private val _adapterSearchLocation: AdapterSearchMallLocation by lazy {
        AdapterSearchMallLocation(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_location,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
        }
        initRcv()
        setArgumens()
        binding.statusView = 2
        setupVisibility(progress = true, data = false)
        setObserver()

        binding.searchForm.doOnTextChanged { text, _, _, _ ->
            vm.params.value = text.toString()
            filterNameMall(text.toString())
        }
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(vm){
                    state.collect {
                        when (it) {
                            SearchMallLocationState.Init -> {
                                setupVisibility(progress = true, data = false)
                            }
                            is SearchMallLocationState.Idle -> {
                                setupVisibility(progress = false, data = true)
                                binding.statusView = 0
                                _adapterSearchLocation.submitList(it.list)
                            }
                            is SearchMallLocationState.OnItemClick -> {
                                _adapterSearchLocation.expandOrCollapseParent(it.locationModel.id)
                            }
                            SearchMallLocationState.Progress -> {
                                binding.statusView = 2
                            }
                            is SearchMallLocationState.SelectLocation -> {
                                val bundle = Bundle()
                                bundle.putLong("locationId", it.locationId)
                                bundle.putLong("subLocationId", it.subLocationId)
                                setFragmentResult(NOTIFICATION_MESSAGE, bundle)
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setArgumens(){
        val listParent : List<CacheParentModel> = args.parentList.toList()
        val listChild : List<SubLocation> = args.childList.toList()
        vm.init(listParent, listChild)
    }

    private fun setupVisibility(progress : Boolean, data : Boolean) {
        binding.progressPage.isVisible = progress
        binding.searchPage.isVisible = data
    }

    private fun initRcv() {
        with(binding) {
            rcvSelectLocationFragment.layoutManager = LinearLayoutManager(requireContext())
            rcvSelectLocationFragment.adapter = _adapterSearchLocation
        }
    }

    private fun filterNameMall(text: String) {
        val filteredlist: ArrayList<LocationModel> = ArrayList()

        for (item in vm.locations) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            val title = requireContext().getString(R.string.title_not_found_location)
            val msg = requireContext().getString(R.string.msg_not_found_location)
            DialogUtils.showErrorDialog(requireContext(), title, msg)
        } else {
            _adapterSearchLocation.submitList(filteredlist)
        }
    }

}