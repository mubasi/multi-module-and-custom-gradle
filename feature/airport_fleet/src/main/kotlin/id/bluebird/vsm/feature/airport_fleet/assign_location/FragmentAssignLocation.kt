package id.bluebird.vsm.feature.airport_fleet.assign_location

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.core.utils.StringUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.FragmentAssignmentFleetLocationBinding
import id.bluebird.vsm.core.utils.ColorUtils
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentAssignLocation : Fragment() {
    companion object {
        const val NOTIFICATION_MESSAGE = "notificationMessage"
        const val MESSAGE = "message"
        const val BACK = "back"
        const val EMPTY_TEXT = ""
    }

    private val vm: AssignLocationViewModel by viewModel()
    private lateinit var mBinding: FragmentAssignmentFleetLocationBinding
    private val args : FragmentAssignLocationArgs by navArgs()
    private var bottomProgressDialog: BottomSheetDialog? = null
    private val assignLocationAdapter: AssignLocationAdapter by lazy {
        AssignLocationAdapter(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_assignment_fleet_location,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVersionCode()
        initRecyclerView()
        initSetArgument()

        with(mBinding) {
            lifecycleOwner = this@FragmentAssignLocation
            vm = this@FragmentAssignLocation.vm
            progressData.isVisible = true
            clContentFragmentAssignLocation.isVisible = false
        }

        vm.getAssignLocation()

        with(vm) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    action.collectLatest {
                        when (it) {
                            is AssignLocationState.Back -> {
                                setFragmentResult(NOTIFICATION_MESSAGE, bundleOf(MESSAGE to BACK))
                                findNavController().navigateUp()
                            }
                            is AssignLocationState.GetListProsess -> {
                                mBinding.progressData.isVisible = true
                                mBinding.clContentFragmentAssignLocation.isVisible = false
                            }
                            is AssignLocationState.SendFleetOnProgress -> {
                                bottomProgressDialog = DialogUtils.progressDialog(
                                    requireContext(),
                                    getString(R.string.assign_fleet_on_progress)
                                )
                            }
                            is AssignLocationState.SelectedCarIsEmpty -> {
                                bottomProgressDialog = DialogUtils.showErrorDialog(
                                    requireContext(),
                                    getString(R.string.list_fleet_is_empty),
                                    EMPTY_TEXT,
                                )
                            }
                            is AssignLocationState.SendCarSuccess -> {
                                bottomProgressDialog?.cancel()
                                val msg =
                                    "${it.countFleet} ${if (it.countFleet is Int) getString(R.string.armada) else ""} ${
                                        StringUtils.getMessage(
                                            requireContext(),
                                            it.location.isNonTerminal,
                                            it.location.isWithPassenger,
                                            it.location.name
                                        )
                                    }"
                                val message = SpannableStringBuilder()
                                    .append(msg)
                                showNotif(
                                    message,
                                    R.color.success_color
                                )
                                setFragmentResult(NOTIFICATION_MESSAGE, bundleOf(MESSAGE to it.countData.toString()))
                                findNavController().navigateUp()
                            }
                            is AssignLocationState.SendCarFromAirport -> {
                                bottomProgressDialog?.cancel()
                                val msg = StringUtils.getMessageRitase(
                                    context = requireContext(),
                                    it.message,
                                    it.isWithPassenger,
                                    it.isStatusArrived
                                )
                                val message = SpannableStringBuilder()
                                    .append(msg)
                                showNotif(
                                    message,
                                    ColorUtils.getColor(it.isWithPassenger, it.isStatusArrived)
                                )
                                setFragmentResult(NOTIFICATION_MESSAGE, bundleOf(MESSAGE to it.countData.toString()))
                                findNavController().navigateUp()
                            }
                            is AssignLocationState.OnError -> {
                                bottomProgressDialog?.cancel()
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    getString(R.string.assign_fleet_error),
                                    it.t.message.toString()
                                )
                            }
                            is AssignLocationState.GetListSuccess -> {
                                mBinding.progressData.isVisible = false
                                mBinding.clContentFragmentAssignLocation.isVisible = true
                                assignLocationAdapter.updateData(it.result)
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

    override fun onDetach() {
        super.onDetach()
        setFragmentResult(NOTIFICATION_MESSAGE, bundleOf(MESSAGE to BACK))
    }

    private fun initSetArgument(){
        if(arguments != null) {
            vm.isPerimeter.value = args.isPerimeter
            vm.selectedCarMap.addAll(args.fleetList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomProgressDialog?.cancel()
    }

    private fun initRecyclerView() {
        mBinding.rcvAssignLocation.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvAssignLocation.adapter = assignLocationAdapter
    }

    private fun setupVersionCode(){
        val manager: PackageManager = requireContext().packageManager
        val info: PackageInfo = manager.getPackageInfo(
            requireContext().packageName, 0
        )
        val version = info.versionCode
        vm.setupVersionCode(version)
    }

    private fun showNotif(message : Spanned, color : Int?) {
        DialogUtils.showSnackbar(
            mBinding.root,
            requireContext(),
            message,
            color,
            null
        )
    }
}