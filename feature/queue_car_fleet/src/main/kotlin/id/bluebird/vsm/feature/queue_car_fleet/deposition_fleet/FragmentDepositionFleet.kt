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
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.DepositionFleetFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDepositionFleet : Fragment() {

    private val viewModel : DepositionFleetViewModel by viewModel()
    private val _args by navArgs<FragmentDepositionFleetArgs>()
    private lateinit var mBinding : DepositionFleetFragmentBinding

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
            showProgress = true
            vm = viewModel
        }
        arguments()
        observe()

    }

    private fun arguments() {
        createTitle(_args.title)
        viewModel.init(_args.subLocationId, _args.depositionStock)
    }

    private fun createTitle(title : String) {
        mBinding.tvLocationNameFleetFragment.text = title
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    actionState.collectLatest {
                        when(it) {
                            is DepositionFleetState.FailedGetList -> {
                                showProgress(false)
                            }
                            DepositionFleetState.GetListEmpty -> {
                                showProgress(false)
                            }
                            is DepositionFleetState.GetListSuccess -> {
                                showProgress(false)
                            }
                            DepositionFleetState.ProgressGetList -> {
                                showProgress(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showProgress(status : Boolean) {
        mBinding.showProgress = status
    }


}