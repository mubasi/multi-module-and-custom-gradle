package id.bluebird.vsm.feature.queue_car_fleet.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.*
import androidx.core.text.bold
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.adapter.AdapterCarFleets
import id.bluebird.vsm.feature.queue_car_fleet.databinding.CarFleetFragmentBinding
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.FragmentAddCarFleet
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.FragmentDepartCarFleetDialog
import id.bluebird.vsm.feature.queue_car_fleet.main.QueueCarFleetViewModel.Companion.EMPTY_STRING
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import id.bluebird.vsm.feature.queue_car_fleet.request_fleet.FragmentRequestCarFleetDialog
import id.bluebird.vsm.feature.queue_car_fleet.ritase_record.FragmentRitaseCarFleetRecordDialog
import id.bluebird.vsm.feature.queue_car_fleet.search_fleet.FragmentSearchCarFleet
import id.bluebird.vsm.navigation.NavigationNav
import id.bluebird.vsm.navigation.NavigationSealed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentQueueCarFleet : Fragment() {

    companion object {
        const val POSITION = 0
    }

    private val _mQueueCarFleetViewModel: QueueCarFleetViewModel by viewModel()
    private lateinit var mBinding: CarFleetFragmentBinding
    private val _fleetAdapter: AdapterCarFleets by inject()
    private val _args by navArgs<FragmentQueueCarFleetArgs>()
    private var bottomProgressDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.car_fleet_fragment,
            container,
            false
        )
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mQueueCarFleetViewModel.stateIdle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            vm = _mQueueCarFleetViewModel
            lifecycleOwner = viewLifecycleOwner
            state = QueueCarFleetState.ProgressHolder
        }
        setHasOptionsMenu(true)
        initRcv()
        observer()
        _mQueueCarFleetViewModel.init()

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(_mQueueCarFleetViewModel) {
                    queueCarFleetState.collectLatest {
                        mBinding.state = it
                        when (it) {
                            QueueCarFleetState.ToSelectLocation -> {
                                NavigationNav.navigate(
                                    NavigationSealed.SelectLocation(
                                        destination = null,
                                        frag = this@FragmentQueueCarFleet,
                                        isMenuFleet = true
                                    )
                                )
                            }
                            is QueueCarFleetState.AddCarFleet -> {
                                navigationToAddFleet(it.subLocationId)
                            }
                            is QueueCarFleetState.SearchCarFleet -> {
                                navigateToSearchFleet()
                            }
                            QueueCarFleetState.GetUserInfoSuccess -> {
                                _fleetAdapter.submitData(listOf())
                                initLocation(_args.locationId, _args.subLocationId)
                                getCounter()
                                getFleetList()
                            }
                            is QueueCarFleetState.GetListSuccess -> {
                                _fleetAdapter.submitData(it.list)
                            }
                            is QueueCarFleetState.NotifyDataCarFleetChanged -> {
                                _fleetAdapter.submitData(it.list)
                            }
                            is QueueCarFleetState.AddFleetSuccess -> {
                                val string = SpannableStringBuilder()
                                    .bold { append(
                                        it.itemAdd.name
                                    ) }
                                    .append(" ")
                                    .append(
                                        getString(R.string.msg_add_fleet_success)
                                    )
                                showSnackbar(string, R.color.success_0)
                                _fleetAdapter.submitData(it.list)
                            }
                            is QueueCarFleetState.GetListEmpty -> {
                                _fleetAdapter.submitData(arrayListOf())
                            }
                            is QueueCarFleetState.ShowRequestCarFleet -> {
                                if (bottomProgressDialog?.isShowing == true) {
                                    bottomProgressDialog?.dismiss()
                                }
                                showProgressDialog(null)
                                delay(500)
                                bottomProgressDialog?.dismiss()
                                showRequestFleet(it.subLocationId)
                            }
                            is QueueCarFleetState.RequestDepartCarFleet -> {
                                FragmentDepartCarFleetDialog(
                                    it.fleet,
                                    it.locationId,
                                    it.subLocationId,
                                    ::departFleet,
                                    ::onErrorFromDialog
                                )
                                    .show(
                                        childFragmentManager,
                                        FragmentDepartCarFleetDialog.TAG
                                    )
                            }
                            is QueueCarFleetState.RecordRitaseToDepart -> {
                                val fragment =
                                    childFragmentManager.findFragmentByTag(
                                        FragmentRitaseCarFleetRecordDialog.TAG
                                    )
                                if (fragment is FragmentRitaseCarFleetRecordDialog) {
                                    fragment.updateQueue(it.queueId)
                                } else {
                                    FragmentRitaseCarFleetRecordDialog(
                                        it.fleet,
                                        it.queueId,
                                        it.locationId,
                                        it.subLocationId,
                                        ::departFleet,
                                        ::showSearchQueue
                                    )
                                        .show(
                                            childFragmentManager,
                                            FragmentRitaseCarFleetRecordDialog.TAG
                                        )
                                }
                            }
                            is QueueCarFleetState.SearchQueueToDepartCar -> {
                                navigateToSearchQueue(
                                    it.fleet,
                                    it.locationId,
                                    it.subLocationId,
                                    it.currentQueueId
                                )
                            }
                            is QueueCarFleetState.SuccessDepartCarFleet -> {
                                successDialogDepartFleet(it.fleetNumber, it.isWithPassenger)
                            }
                            is QueueCarFleetState.FailedDepart -> {
                                val string = SpannableStringBuilder()
                                    .bold { append(getString(R.string.fleet)) }
                                    .append(" ")
                                    .append(getString(R.string.failed_depart_description))

                                showSnackbar(string, R.color.warning_0)
                            }
                            is QueueCarFleetState.FailedGetQueueCar -> {
                                val string = SpannableStringBuilder()
                                    .append(
                                        it.throwable.message
                                            ?: requireContext().getString(R.string.failed_get_queue)
                                    )

                                showSnackbar(string, R.color.warning_0)
                            }
                            is QueueCarFleetState.GoToQrCodeScreen -> {
                                gotoQrcodeScreen(it.locationId, it.subLocationId, it.titleLocation)
                            }
                            is QueueCarFleetState.GotoDepositionScreen -> {
                                val bundle = Bundle()
                                bundle.putString("title", it.title)
                                bundle.putLong("sub_location_id", it.subLocationId)
                                bundle.putLong("deposition_stock", it.depositionStock)
                                bundle.putLong("id_deposition", it.idDeposition)
                                findNavController().navigate(R.id.depositionFleetFragment, bundle)
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

    private fun successDialogDepartFleet(fleetNumber: String, isWithPassenger: Boolean) {
        val string = SpannableStringBuilder()
            .bold { append(fleetNumber) }
            .append(" ")
            .append(
                if (isWithPassenger) getString(R.string.fleet_depart_with_passenger)
                else getString(R.string.fleet_depart_without_passenger)
            )
        showSnackbar(string, R.color.success_0)
        _mQueueCarFleetViewModel.removeFleet(fleetNumber)
    }

    private fun showSnackbar(message: Spanned, color: Int) {
        DialogUtils.showSnackbar(requireView(), requireContext() , message, color, null)
    }

    private fun showProgressDialog(message: String?) {
        bottomProgressDialog = DialogUtils.progressDialog(requireContext(), message)
    }

    private fun showRequestFleet(subLocationId: Long) {
        FragmentRequestCarFleetDialog(
            subLocationId,
            _mQueueCarFleetViewModel::updateRequestCount
        ).show(
            childFragmentManager,
            FragmentRequestCarFleetDialog.TAG
        )
    }

    private fun navigateToSearchFleet() {
        findNavController().navigate(R.id.action_queueCarFleetFragment_to_searchFragment)
        setFragmentResultListener(FragmentSearchCarFleet.RESULT_SEARCH) { _, bundle ->
            val fleet = bundle.getParcelable<CarFleetItem>(FragmentSearchCarFleet.REQUEST_SEARCH)
                ?: return@setFragmentResultListener
            _mQueueCarFleetViewModel.requestDepart(fleet)
        }
    }

    private fun navigateToSearchQueue(
        carFleetItem: CarFleetItem,
        locationId: Long,
        subLocationId: Long,
        currentQueueId: String,
    ) {
        val destination =
            FragmentQueueCarFleetDirections.actionQueueCarFleetFragmentToSearchQueueFragment()
                .apply {
                    this.location = locationId
                    this.subLocation = subLocationId
                    this.currentQueue = currentQueueId
                }

        setFragmentResultListener(FragmentAddCarFleet.REQUEST_SELECT) { _, bundle ->
            _mQueueCarFleetViewModel.showRecordRitase(
                carFleetItem,
                bundle.getString(FragmentAddCarFleet.RESULT_SELECT) ?: EMPTY_STRING
            )
        }
    }

    private fun navigationToAddFleet(subLocationId: Long) {
        with(findNavController()) {
            if (currentDestination?.id == R.id.queueCarFleetFragment) {
                val destination = FragmentQueueCarFleetDirections.actionQueueFleetFragmentToAddFleetFragment()
                        .apply {
                            subLocation = subLocationId
                        }
                navigate(destination)
            }
        }
        setFragmentResultListener(FragmentAddCarFleet.RESULT) { _, bundle ->
            val result = bundle.getString(FragmentAddCarFleet.REQUEST_ADD_NUMBER)
            if(result != null) {
                val string = SpannableStringBuilder()
                    .bold { append(result) }
                    .append(" ")
                    .append(
                        getString(R.string.msg_add_fleet_success)
                    )
                showSnackbar(string, R.color.success_0)
            }
            _mQueueCarFleetViewModel.addSuccess(bundle.getParcelable(FragmentAddCarFleet.REQUEST_ADD))
        }
    }

    private fun initRcv() {
        with(mBinding) {
            _fleetAdapter.initViewModel(_mQueueCarFleetViewModel)
            mainListFleetFleetFragment.adapter = _fleetAdapter
            mainListFleetFleetFragment.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.queue_fleet_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.qr_code_screen -> {
                _mQueueCarFleetViewModel.goToQrCodeScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun gotoQrcodeScreen(locationId: Long, subLocationId: Long, titleLocation: String) {
        NavigationNav.navigate(
            NavigationSealed.QrCode(
                destination = null,
                frag = this@FragmentQueueCarFleet,
                locationId = locationId,
                subLocationId = subLocationId,
                titleLocation = titleLocation,
                position = POSITION.toLong()
            )
        )
    }
}