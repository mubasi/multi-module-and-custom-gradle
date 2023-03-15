package id.bluebird.vsm.feature.home.queue_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.QueueSearchFragmentBinding
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.queue_search.QueueSearchViewModel.Companion.EMPTY_STRING
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentQueueSearch : Fragment() {

    private lateinit var binding: QueueSearchFragmentBinding
    private val _queueSearchViewModel: QueueSearchViewModel by viewModel()
    private var prefix: String = EMPTY_STRING
    private var adapterSearchCache = AdapterSearchQueue()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.queue_search_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vm = _queueSearchViewModel
            this.state = QueueSearchState.Idle
        }

        setArgument()
        setupListQueue()
        setupPrefixInSearch(prefix)
        setupListenerSearch()
        setupChipGroup()
        visibleIconClear(false)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_queueSearchViewModel) {
                    queueSearchState.collectLatest {
                        binding.state = it
                        when (it) {
                            is QueueSearchState.FilterResult -> {
                                adapterSearchCache.submitList(
                                    it.result
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

    private fun setupListQueue() {
        with(binding) {
            recyclerViewQueue.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewQueue.adapter = adapterSearchCache
        }
    }

    private fun setupPrefixInSearch(prefix: String) {
        binding.prefixPages.text = "$prefix."
    }

    private fun setupListenerSearch() {
        binding.searchForm.doOnTextChanged { text, _, _, _ ->
            val lengthText = text?.length ?: 0
            visibleIconClear(lengthText > 0)
            if (lengthText == 0) {
                _queueSearchViewModel.clearSearch()
            } else if (lengthText > 5) {
                _queueSearchViewModel.errorState()
            } else {
                _queueSearchViewModel.params.value = text.toString()
                _queueSearchViewModel.filterQueue()
            }
        }
    }

    private fun visibleIconClear(result: Boolean) {
        binding.clearSearch.isVisible = result
    }

    private fun setArgument() {
        prefix = arguments?.getString("prefix") ?: EMPTY_STRING
        val listWaiting = arguments?.getParcelableArrayList<QueueReceiptCache>("listWaiting")
        val listSkipped = arguments?.getParcelableArrayList<QueueReceiptCache>("listSkipped")
        if (listWaiting != null && listSkipped != null) {
            _queueSearchViewModel.init(
                listWaiting, listSkipped, prefix
            )
        }
    }

    private fun setupChipGroup() {
        binding.filterAll.isChecked = true
        binding.filterPage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.filterWaiting.id -> {
                    _queueSearchViewModel.setFilterStatus(
                        QueueSearchViewModel.StatusFilter.WAITING
                    )
                }
                binding.filterSkipped.id -> {
                    _queueSearchViewModel.setFilterStatus(
                        QueueSearchViewModel.StatusFilter.SKIPPED
                    )
                }
                else -> {
                    _queueSearchViewModel.setFilterStatus(
                        QueueSearchViewModel.StatusFilter.ALL
                    )
                }
            }
            if (_queueSearchViewModel.params.value?.isNotEmpty() == true) {
                _queueSearchViewModel.filterQueue()
            }
        }
    }

}