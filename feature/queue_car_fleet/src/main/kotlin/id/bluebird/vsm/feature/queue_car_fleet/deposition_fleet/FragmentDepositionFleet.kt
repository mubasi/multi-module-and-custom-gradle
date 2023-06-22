package id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet

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
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.DepositionFleetFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDepositionFleet : Fragment() {

    private val viewModel : DepositionFleetViewModel by viewModel()
    private val _args by navArgs<FragmentDepositionFleetArgs>()
    private lateinit var mBinding : DepositionFleetFragmentBinding
    private val adapterDepositionFleet: AdapterDepositionFleet by lazy {
        AdapterDepositionFleet(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.deposition_fleet_fragment,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            state = DepositionFleetState.ProgressGetList
            vm = viewModel
        }
        arguments()
        observe()
        initRcv()
    }

    private fun arguments() {
        viewModel.init(_args.idDeposition, _args.depositionStock, _args.title)
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    actionState.collectLatest {
                        mBinding.state = it
                        when(it) {
                            is DepositionFleetState.FailedGetList -> {
                                showDialogError(it.result.message ?: getString(R.string.msg_can_not_show_list_deposition))
                            }
                            is DepositionFleetState.GetListSuccess -> {
                                adapterDepositionFleet.updateData(it.result)
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

    private fun initRcv() {
        with(mBinding) {
            fleetListRcv.layoutManager = LinearLayoutManager(requireContext())
            fleetListRcv.adapter = adapterDepositionFleet
        }
    }

    private fun showDialogError(msg : String) {
        DialogUtils.showErrorDialog(
            requireContext(),
            null,
            msg
        )
    }

}