package id.bluebird.vsm.feature.select_location.search_mall_location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.select_location.R
import id.bluebird.vsm.feature.select_location.SelectLocationState
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.select_location.adapter.AdapterSelectLocation
import id.bluebird.vsm.feature.select_location.databinding.FragmentSearchLocationBinding
import id.bluebird.vsm.feature.select_location.model.LocationModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
class FragmentSearchMallLocation : Fragment() {

    companion object {
        const val NOTIFICATION_MESSAGE = "notificationMessage"
        const val STATUS_SEARCH = "search"
        const val BACK = "back"
    }

    private val vm : SelectLocationViewModel by sharedViewModel()
    private lateinit var binding : FragmentSearchLocationBinding
    private val _adapterSearchLocation: AdapterSelectLocation by lazy {
        AdapterSelectLocation(vm)
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
        _adapterSearchLocation.submitList(vm._locations)
        binding.statusView = 0
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
                            is SelectLocationState.OnItemClick -> {
                                _adapterSearchLocation.expandOrCollapseParent(it.locationModel.id)
                            }
                            is SelectLocationState.ToAssign -> {
                                setFragmentResult(NOTIFICATION_MESSAGE, bundleOf(STATUS_SEARCH to BACK))
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initRcv() {
        with(binding) {
            rcvSelectLocationFragment.layoutManager = LinearLayoutManager(requireContext())
            rcvSelectLocationFragment.adapter = _adapterSearchLocation
        }
    }

    private fun filterNameMall(text: String) {
        val filteredlist: ArrayList<LocationModel> = ArrayList()

        for (item in vm._locations) {
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