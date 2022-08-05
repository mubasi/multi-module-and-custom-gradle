package id.bluebird.mall.feature_queue_fleet.add_fleet

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.mall.core.extensions.checkIfIntegerIsGtThanZero
import id.bluebird.mall.core.utils.DialogUtils
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.add_fleet.adapter.AddFleetAdapter
import id.bluebird.mall.feature_queue_fleet.databinding.AddFleetFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFleetFragment : Fragment() {
    companion object {
        const val REQUEST_ADD = "requestAdd"
        const val RESULT = "resultAdd"
        const val REQUEST_SELECT = "requestSelectQueue"
        const val RESULT_SELECT = "resultSelect"
    }

    private val _args: AddFleetFragmentArgs by navArgs()
    private lateinit var _mBinding: AddFleetFragmentBinding
    private val _vm: AddFleetViewModel by viewModel()
    private val _addFleetAdapter: AddFleetAdapter by lazy {
        AddFleetAdapter(_vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = DataBindingUtil.inflate(inflater, R.layout.add_fleet_fragment, container, false)
        return _mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(_mBinding) {
            lifecycleOwner = viewLifecycleOwner
            state = AddFleetState.OnProgressGetList
            vm = _vm
            isSearchQueue = _args.isSearchQueue
        }
        _vm.init(_args.subLocation, _args.isSearchQueue)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _vm.addFleetState.collectLatest {
                    _mBinding.state = it
                    when (it) {
                        is AddFleetState.AddError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.add_fleet_failed_title),
                                getString(R.string.add_fleet_failed_message)
                            )
                                .show()
                        }
                        is AddFleetState.SearchError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.search_flett_error_title),
                                getString(R.string.search_flett_error_message)
                            )
                                .show()
                        }
                        is AddFleetState.QueueSearchError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.search_flett_error_title),
                                getString(R.string.search_queue_error_message)
                            )
                                .show()
                        }
                        AddFleetState.GetListEmpty -> {
                            _addFleetAdapter.submitList(ArrayList())
                        }
                        is AddFleetState.GetListSuccess -> {
                            _addFleetAdapter.submitList(it.list)
                        }
                        is AddFleetState.SuccessGetQueue -> {
                            _addFleetAdapter.submitList(it.list)
                        }
                        is AddFleetState.UpdateSelectPosition -> updateAdapterPosition(
                            it.lastPosition,
                            it.newPosition
                        )
                        is AddFleetState.AddFleetSuccess -> {
                            val bundle = Bundle()
                            bundle.putParcelable(REQUEST_ADD, it.fleetItem)
                            setFragmentResult(RESULT, bundle)
                            findNavController().popBackStack()
                        }
                        is AddFleetState.FinishSelectQueue -> {
                            val bundle = Bundle()
                            bundle.putString(RESULT_SELECT, it.number)
                            setFragmentResult(REQUEST_SELECT, bundle)
                            findNavController().popBackStack()
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
        initRecyclerview()
        if (!_args.isSearchQueue)
            _vm.searchFleet()
    }


    private fun updateAdapterPosition(lastPosition: Int, newPosition: Int) {
        try {
            lastPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _addFleetAdapter.notifyItemChanged(lastPosition)
                }
            }
            newPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _addFleetAdapter.notifyItemChanged(newPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRecyclerview() {
        _mBinding.listItemFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        _mBinding.listItemFleetFragment.adapter = _addFleetAdapter
    }
}