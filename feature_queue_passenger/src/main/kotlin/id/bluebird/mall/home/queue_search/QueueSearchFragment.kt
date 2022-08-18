package id.bluebird.mall.home.queue_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import id.bluebird.mall.home.databinding.QueueSearchFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import id.bluebird.mall.home.R
import id.bluebird.mall.home.dialog_delete_skipped.DialogDeleteSkipped
import id.bluebird.mall.home.dialog_restore_skipped.DialogRestoreSkipped
import id.bluebird.mall.home.dialog_skip_queue.DialogSkipQueueFragment
import id.bluebird.mall.home.main.CustomAdapter
import id.bluebird.mall.home.main.CustomAdapterSkipped
import id.bluebird.mall.home.main.QueuePassengerState
import id.bluebird.mall.home.main.QueuePassengerViewModel
import proto.QueuePangkalanOuterClass
import androidx.appcompat.app.AppCompatActivity


class QueueSearchFragment : Fragment() {
    companion object {
        const val REQUEST_SEARCH = "requestSearch"
        const val RESULT_SEARCH = "resultSearch"
    }

    private lateinit var binding: QueueSearchFragmentBinding
    private val _queueSearchViewModel: QueueSearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.queue_search_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vm = _queueSearchViewModel
        }

        binding.statusView = 1

        val locationId = arguments?.getLong("locationId") ?: 0
        val subLocationId = arguments?.getLong("subLocationId") ?: 0
        val type = arguments?.getInt("type") ?: 0

        setupListQueue()
        setupLabelAppBar(type)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(_queueSearchViewModel) {
                    queueSearchState.collect {
                        when (it) {
                            QueueSearchState.ProsesSearchQueue -> {
                                binding.statusView = 2
                                if (type > 0) {
                                    searchFilter(locationId,
                                        subLocationId,
                                        QueuePangkalanOuterClass.QueueType.SEARCH_SKIPPED_QUEUE)
                                } else {
                                    searchFilter(locationId,
                                        subLocationId,
                                        QueuePangkalanOuterClass.QueueType.SEARCH_WAITING_QUEUE)
                                }
                            }
                            QueueSearchState.SuccessSearchQueue -> {
                                binding.statusView = 0
                                setupList(type)
                            }
                            is QueueSearchState.FailedSearchQueue -> {
                                binding.statusView = 3
                            }
                            is QueueSearchState.ProsesDeleteQueueSkipped -> {
                                val currentData = it.queueReceiptCache
                                DialogDeleteSkipped(
                                    number = currentData.queueNumber,
                                    queueId = currentData.queueId,
                                    locationId = locationId,
                                    subLocationId = subLocationId
                                ).show(requireActivity().supportFragmentManager,
                                    DialogSkipQueueFragment.TAG)
                            }
                            is QueueSearchState.ProsesRestoreQueueSkipped -> {
                                val currentData = it.queueReceiptCache
                                DialogRestoreSkipped(
                                    number = currentData.queueNumber,
                                    queueId = currentData.queueId,
                                    locationId = locationId,
                                    subLocationId = subLocationId
                                ).show(requireActivity().supportFragmentManager,
                                    DialogRestoreSkipped.TAG)
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListQueue() {
        binding.recyclerViewQueue.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupList(type: Int) {
        if (type > 0) {
            val adapter = SearchQueueAdapterSkipped(_queueSearchViewModel.listQueue.queues,
                _queueSearchViewModel)
            binding.recyclerViewQueue.adapter = adapter
        } else {
            val adapter = CustomAdapter(_queueSearchViewModel.listQueue.queues)
            binding.recyclerViewQueue.adapter = adapter
        }
    }

    private fun setupLabelAppBar(type: Int) {
        var currentTitle = ""

        currentTitle = if (type > 0) {
            "Cari antrian tertunda"
        } else {
            "Cari antrian menunggu"
        }

        (activity as AppCompatActivity).supportActionBar?.title = currentTitle
    }

}