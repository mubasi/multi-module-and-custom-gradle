package id.bluebird.vsm.feature.user_management.search_location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.user_management.R
import id.bluebird.vsm.feature.user_management.databinding.FragmentUserSearchLocationBinding
import id.bluebird.vsm.feature.user_management.search_location.adapter.AdapterSearchLocation
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSearchLocation : Fragment() {

    companion object {
        const val REQUEST_KEY = "searchLocationRequest"
        const val RESULT_KEY = "searchLocationResult"
    }

    private lateinit var mBinding: FragmentUserSearchLocationBinding
    private val searchLocationViewModel: SearchLocationViewModel by viewModel()
    private val adapter: AdapterSearchLocation by lazy {
        AdapterSearchLocation(searchLocationViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_search_location,
                container,
                false
            )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = searchLocationViewModel
            state = SearchLocationState.OnProgressGetList
        }

        searchLocationViewModel.init()
        initRv()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                searchLocationViewModel.searchState.collect {
                    mBinding.state = it
                    when (it) {
                        is SearchLocationState.FailedGetList -> {}
                        is SearchLocationState.Success -> {
                            adapter.submitList(it.data)
                        }
                        is SearchLocationState.UpdateSelectedLocation -> {
                            adapter.setSelected(it.item)
                        }
                        is SearchLocationState.SetSelected -> {
                            val bundle = Bundle()
                            bundle.putParcelable(RESULT_KEY, it.item)
                            setFragmentResult(REQUEST_KEY, bundle)
                            findNavController().popBackStack()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun initRv() {
        mBinding.listLocation.apply {
            adapter = this@FragmentSearchLocation.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}