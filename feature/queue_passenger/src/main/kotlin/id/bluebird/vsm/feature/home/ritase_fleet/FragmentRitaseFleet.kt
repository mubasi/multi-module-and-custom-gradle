package id.bluebird.vsm.feature.home.ritase_fleet

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.core.extensions.checkIfIntegerIsGtThanZero
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.FragmentRitaseFleetBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRitaseFleet : Fragment() {

    companion object {
        const val RITASE_FLEET = "ritaseFleet"
        const val FLEET_NUMBER = "fleetNumber"
    }

    private lateinit var binding: FragmentRitaseFleetBinding
    private val vm: RitaseFleetViewModel by viewModel()
    private var userId: Long = -1
    private var locationId: Long = -1
    private var subLocationId: Long = -1
    private val _adapterListFleet: AdapterListFleet by lazy {
        AdapterListFleet(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ritase_fleet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initRefreshLayout()
        setVisible(progress = true, 1)
        setObserve()
        setAdapter()
        setButtonAction()
        setFilter()
        setArgument()

    }

    private fun setArgument() {
        if (arguments != null) {
            userId = arguments?.getLong("userId") ?: -1
            locationId = arguments?.getLong("locationId") ?: -1
            subLocationId = arguments?.getLong("subLocationId") ?: -1
            vm.init(userId, locationId, subLocationId)
        }
    }

    private fun setObserve() {
        vm.selectedFleetNumber.observe(viewLifecycleOwner) {
            setBackgroundButton(it.isNotEmpty())
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(vm) {
                    ritaseFleetState.collect {
                        when(it) {
                            is RitaseFleetState.ProsesListFleet -> {
                                setVisible(progress = true, 1)
                            }
                            is RitaseFleetState.CurrentQueueNotFound -> {
                                val msg = requireContext().getString(R.string.current_queue_not_found)
                                val message = SpannableString(msg)
                                DialogUtils.showSnackbar(requireView(), message, R.color.error_color)
                                findNavController().popBackStack()
                            }
                            is RitaseFleetState.FailedGetList -> {
                                setVisible(progress = false, 2)
                                val title = requireContext().getString(R.string.fleet_not_found)
                                val msg = requireContext().getString(R.string.msg_fleet_not_found)
                                DialogUtils.showErrorDialog(requireContext(), title, msg)
                            }
                            RitaseFleetState.GetListEmpty -> {
                                setVisible(progress = false, statusView = 2)
                            }
                            is RitaseFleetState.GetListSuccess -> {
                                setVisible(progress = false, 0)
                                _adapterListFleet.setData(it.result)
                            }
                            is RitaseFleetState.FilterFleet -> {
                                setVisible(progress = false, 0)
                                _adapterListFleet.setData(it.result)
                            }
                            is RitaseFleetState.FilterFleetFailed -> {
                                setVisible(progress = false, 2)
                            }
                            is RitaseFleetState.CancleFleet -> {
                                findNavController().popBackStack()
                            }
                            RitaseFleetState.FleetNotSelected -> {
                                val title = requireContext().getString(R.string.title_fleet_not_selected)
                                val msg = requireContext().getString(R.string.msg_fleet_not_selected)
                                DialogUtils.showErrorDialog(requireContext(), title, msg)
                            }
                            is RitaseFleetState.SuccessSaveFleet -> {
                                setFragmentResult(RITASE_FLEET, bundleOf(FLEET_NUMBER to it.fleetNumber))
                                val navController = findNavController()
                                navController.popBackStack()
                            }
                            is RitaseFleetState.UpdateSelectPosition -> updateAdapterPosition(
                                it.lastPosition,
                                it.newPosition
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setVisible(progress : Boolean, statusView : Int){
        if(progress) {
            binding.statusView = 1
            binding.swipeRefreshLayout.isRefreshing = true
        } else {
            binding.statusView = 0
            binding.swipeRefreshLayout.isRefreshing = false
            binding.statusEdit = true
        }
        binding.statusView = statusView
    }

    private fun setAdapter(){
        binding.rcvSelectFleet.apply {
            adapter = _adapterListFleet
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.init(userId, locationId, subLocationId)
        }
    }

    private fun setButtonAction(){
        binding.btnSaveFleet.setOnClickListener {
            vm.saveFleet()
        }
    }
    
    private fun setBackgroundButton(notEmpty : Boolean) {
        var colorButton = R.color.gray_disable
        var colorBackground = R.drawable.bg_button_disable

        if(notEmpty) {
            colorButton = R.color.white
            colorBackground = R.drawable.bg_submit
        }

        binding.btnSaveFleet.setTextColor(ContextCompat.getColor(requireContext(), colorButton))
        binding.btnSaveFleet.background = ContextCompat.getDrawable(requireContext(), colorBackground)
    }

    private fun setFilter(){
        binding.searchForm.doOnTextChanged { text, _, _, _ ->
            vm.clearSelected()
            vm.params.value = text.toString()
            vm.filterFleet()
        }
    }

    private fun updateAdapterPosition(lastPosition: Int, newPosition: Int) {
        try {
            lastPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _adapterListFleet.notifyItemChanged(lastPosition)
                }
            }
            newPosition.checkIfIntegerIsGtThanZero().let { bool ->
                if (bool) {
                    _adapterListFleet.notifyItemChanged(newPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}