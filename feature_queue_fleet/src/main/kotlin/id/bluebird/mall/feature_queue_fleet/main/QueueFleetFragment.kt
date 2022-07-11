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
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFleetFragment : Fragment() {

    private val mQueueFleetViewModel: QueueFleetViewModel by viewModel()
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
            vm = mQueueFleetViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        mQueueFleetViewModel.initUserId(null)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            with(mQueueFleetViewModel) {
                queueFleetState.collectLatest {
                    when (it) {
                        QueueFleetState.GetUserInfoSuccess -> {
                            getCounter()
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