package id.bluebird.mall.feature_queue_fleet.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.databinding.FleetFragmentBinding
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFleetFragment : Fragment() {

    private val _mQueueFleetViewModel: QueueFleetViewModel by viewModel()
    private lateinit var mBinding: FleetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fleet_fragment,
            container,
            false
        )
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            vm = _mQueueFleetViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        _mQueueFleetViewModel.initUserId(null)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            with(_mQueueFleetViewModel) {
                queueFleetState.collect {
                    when (it) {
                        QueueFleetState.GetUserInfoSuccess -> {
                            getCounter()
                        }
                        is QueueFleetState.ShowRequestFleet -> {
                            RequestFleetDialog(
                                it.subLocationId,
                                _mQueueFleetViewModel::updateRequestCount
                            ).show(
                                childFragmentManager,
                                RequestFleetDialog.TAG
                            )
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