package id.bluebird.vsm.feature.airport_fleet.request_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.FragmentRequestListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRequestList : Fragment() {

    private val viewModel: RequestListViewModel by viewModel()
    private lateinit var binding: FragmentRequestListBinding
    private val _args: FragmentRequestListArgs by navArgs()
    private val _adapter: AdapterRequestList by lazy {
        AdapterRequestList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_request_list,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArguments()
        setupBinding()
        observe()
        initRcv()
    }

    private fun setupBinding() {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            showProgress = true
        }
    }

    private fun setupArguments() {
        viewModel.init(_args.isWing)
    }

    private fun initRcv() {
        binding.itemList.apply {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    state.collectLatest {
                        when (it) {
                            is RequestListState.Progress -> {
                                showProgress(true)
                            }
                            is RequestListState.Success -> {
                                _adapter.submitList(it.data)
                                showProgress(false)
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

    private fun showProgress(status : Boolean) {
        binding.showProgress = status
    }


}