package id.bluebird.vsm.feature.queue_fleet.add_fleet

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.extensions.checkIfIntegerIsGtThanZero
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.add_by_camera.FragmentAddByCamera
import id.bluebird.vsm.feature.queue_fleet.add_fleet.adapter.AdapterAddFleet
import id.bluebird.vsm.feature.queue_fleet.databinding.AddFleetFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.result.contract.ActivityResultContracts

class FragmentAddFleet : Fragment() {
    companion object {
        const val REQUEST_ADD = "requestAdd"
        const val RESULT = "resultAdd"
        const val REQUEST_SELECT = "requestSelectQueue"
        const val RESULT_SELECT = "resultSelect"
        var PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
        )

    }

    private val _args: FragmentAddFleetArgs by navArgs()
    private lateinit var _mBinding: AddFleetFragmentBinding
    private val _vm: AddFleetViewModel by viewModel()
    private val _AdapterAddFleet: AdapterAddFleet by lazy {
        AdapterAddFleet(_vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        _vm.init(_args.location, _args.subLocation, _args.isSearchQueue)
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
                            _AdapterAddFleet.submitList(ArrayList())
                        }
                        is AddFleetState.GetListSuccess -> {
                            _AdapterAddFleet.submitList(it.list)
                        }
                        is AddFleetState.SuccessGetQueue -> {
                            _AdapterAddFleet.submitList(it.list)
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
        addByCamera()
        if (!_args.isSearchQueue)
            _vm.searchFleet()
    }

    private fun updateAdapterPosition(lastPosition: Int, newPosition: Int) {
        try {
            lastPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _AdapterAddFleet.notifyItemChanged(lastPosition)
                }
            }
            newPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _AdapterAddFleet.notifyItemChanged(newPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRecyclerview() {
        _mBinding.listItemFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        _mBinding.listItemFleetFragment.adapter = _AdapterAddFleet
    }

    private fun addByCamera() {
        _mBinding.addByCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permReqLauncher.launch(PERMISSIONS)
            } else {
                navAddByCamera()
            }
        }
    }

    private fun navAddByCamera() {
        val destination = FragmentAddFleetDirections.actionQueueFleetFragmentToAddByCamera()
        findNavController().navigate(destination)
        setFragmentResultListener(FragmentAddByCamera.RESULT) { _, bundle ->
            var temp = bundle.getString(FragmentAddByCamera.RESULT_ADD)
            var lambungNumber = temp.toString().replace("\\s".toRegex(), "")
            _vm.resultScan(lambungNumber)
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                navAddByCamera()
            }
        }
}