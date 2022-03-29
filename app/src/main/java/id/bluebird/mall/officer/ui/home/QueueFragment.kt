package id.bluebird.mall.officer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.databinding.TabFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QueueFragment(private val fragmentType: FragmentType) : Fragment() {
    private val mHomeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var mBinding: TabFragmentBinding
    private val mQueueAdapter: QueueAdapter by lazy {
        QueueAdapter(fragmentType, mHomeViewModel)
    }
    private lateinit var rcvHome: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate<TabFragmentBinding>(
            inflater,
            R.layout.tab_fragment,
            container,
            false
        )
        rcvHome = mBinding.rcvHome
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.clTabFragment.requestLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rcvHome.layoutManager = linearLayoutManager
        rcvHome.isNestedScrollingEnabled = true
        rcvHome.adapter = mQueueAdapter
        listen()
    }

    private fun listen() {
        mHomeViewModel.let { vm ->
            if (fragmentType == FragmentType.DELAY) {
                vm.queueDelay.observe(viewLifecycleOwner) {
                    mQueueAdapter.setItems(it)
                }
            } else {
                vm.queueWaiting.observe(viewLifecycleOwner) {
                    mQueueAdapter.setItems(it)
                }
            }
        }
    }
}

enum class FragmentType {
    DELAY, WAITING
}