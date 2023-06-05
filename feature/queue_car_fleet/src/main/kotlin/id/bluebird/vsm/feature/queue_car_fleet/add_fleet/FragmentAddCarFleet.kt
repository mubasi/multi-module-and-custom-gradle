package id.bluebird.vsm.feature.queue_car_fleet.add_fleet

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
import id.bluebird.vsm.feature.queue_car_fleet.add_by_camera.FragmentAddCarFleetByCamera
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.adapter.AdapterAddCarFleet
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.result.contract.ActivityResultContracts
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.AddCarFleetFragmentBinding

class FragmentAddCarFleet : Fragment() {
    companion object {
        const val REQUEST_ADD = "requestAdd"
        const val RESULT = "resultAdd"
        const val REQUEST_SELECT = "requestSelectQueue"
        const val RESULT_SELECT = "resultSelect"
        var PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
        )

    }

    private val _args: FragmentAddCarFleetArgs by navArgs()
    private lateinit var _mBinding: AddCarFleetFragmentBinding
    private val _vm: AddCarFleetViewModel by viewModel()
    private val _adapterAddCarFleet: AdapterAddCarFleet by lazy {
        AdapterAddCarFleet(_vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _mBinding = DataBindingUtil.inflate(inflater, R.layout.add_car_fleet_fragment, container, false)
        return _mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(_mBinding) {
            lifecycleOwner = viewLifecycleOwner
            state = AddCarFleetState.OnProgressGetList
            vm = _vm
            isSearchQueue = _args.isSearchQueue
        }
        _vm.init(_args.location, _args.subLocation, _args.isSearchQueue)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _vm.addCarFleetState.collectLatest {
                    _mBinding.state = it
                    when (it) {
                        is AddCarFleetState.AddCarError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.add_fleet_failed_title),
                                it.err.message ?: getString(R.string.add_fleet_failed_message)
                            )
                                .show()
                        }
                        is AddCarFleetState.SearchError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.search_flett_error_title),
                                it.err.message ?: getString(R.string.search_flett_error_message)
                            )
                                .show()
                        }
                        is AddCarFleetState.QueueSearchError -> {
                            DialogUtils.showErrorDialog(
                                requireContext(),
                                getString(R.string.search_flett_error_title),
                              it.err.message ?:   getString(R.string.search_queue_error_message)
                            )
                                .show()
                        }
                        AddCarFleetState.GetListEmpty -> {
                            _adapterAddCarFleet.submitList(ArrayList())
                        }
                        is AddCarFleetState.GetListSuccess -> {
                            _adapterAddCarFleet.submitList(it.list)
                        }
                        is AddCarFleetState.SuccessGetQueue -> {
                            _adapterAddCarFleet.submitList(it.list)
                        }
                        is AddCarFleetState.UpdateSelectPosition -> updateAdapterPosition(
                            it.lastPosition,
                            it.newPosition
                        )
                        is AddCarFleetState.AddCarFleetSuccess -> {
                            val bundle = Bundle()
                            bundle.putParcelable(REQUEST_ADD, it.carFleetItem)
                            setFragmentResult(RESULT, bundle)
                            findNavController().popBackStack()
                        }
                        is AddCarFleetState.FinishSelectQueue -> {
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
                    _adapterAddCarFleet.notifyItemChanged(lastPosition)
                }
            }
            newPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _adapterAddCarFleet.notifyItemChanged(newPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRecyclerview() {
        _mBinding.listItemFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        _mBinding.listItemFleetFragment.adapter = _adapterAddCarFleet
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
        val destination =
            FragmentAddCarFleetDirections.actionQueueFleetFragmentToAddByCamera()
        findNavController().navigate(destination)
        setFragmentResultListener(FragmentAddCarFleetByCamera.RESULT) { _, bundle ->
            val temp = bundle.getString(FragmentAddCarFleetByCamera.RESULT_ADD)
            val lambungNumber = temp.toString().replace("\\s".toRegex(), "")
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