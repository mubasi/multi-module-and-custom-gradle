package id.bluebird.vsm.feature.airport_fleet.add_fleet

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.add_fleet.AddFleetState
import id.bluebird.vsm.feature.airport_fleet.databinding.FragmentAddFleetNonApshBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentAddFleet : Fragment() {

    private lateinit var mBinding: FragmentAddFleetNonApshBinding
    private val args: FragmentAddFleetArgs by navArgs()
    private val _vm: AddFleetViewModelNonApsh by viewModel()
    private val _adapterAddFleet: AdapterAddFleet by lazy {
        AdapterAddFleet(vm = this._vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_fleet_non_apsh,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            vm = _vm
            state = AddFleetState.ProgressSearch
        }

        args.apply {
            _vm.init(isPerimeter, subLocationId)
        }

        initRcv()

        with(_vm) {
            param.observe(viewLifecycleOwner) {
                mBinding.btnAddStock.isEnabled = it.isNotEmpty()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    addFleetState.collectLatest {
                        mBinding.state = it
                        when (it) {
                            is AddFleetState.AddFleetError -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    message = it.throwable.message ?: getString(R.string.error_unknown),
                                    title = getString(R.string.add_fleet_failed)
                                )
                            }
                            is AddFleetState.SearchFleetError -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    message = it.throwable.message ?: getString(R.string.error_unknown),
                                    title = getString(R.string.search_fleet_failed)
                                )
                            }
                            is AddFleetState.SearchFleetSuccess -> {
                                _adapterAddFleet.updateList(it.list)
                            }
                            AddFleetState.FleetsReset -> {
                                _adapterAddFleet.updateList(ArrayList())
                            }
                            is AddFleetState.ShowDialogAddFleet -> {
                                DialogUtils.showDialogAddFleet(requireContext(), it.fleetNumber) { isYes ->
                                    if (isYes) {
                                       addFleetFromButton(it.fleetNumber, false)
                                    }
                                }
                            }
                            is AddFleetState.AddFleetSuccess -> {
                                val msg =
                                    "${it.fleetNumber} ${getString(R.string.car_success_add_in_stock)}"
                                val message = SpannableStringBuilder()
                                    .append(msg)
                                DialogUtils.showSnackbar(view, requireContext(), message, R.color.success_color, null)
                                findNavController().navigateUp()
                            }
                            else -> {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initRcv() {
        with(mBinding) {
            rcvAddFleet.layoutManager = LinearLayoutManager(requireContext())
            rcvAddFleet.adapter = _adapterAddFleet
        }

        mBinding.edtSearchFleet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! > 3) {
                    _vm.filterFleet(p0.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
    }
}