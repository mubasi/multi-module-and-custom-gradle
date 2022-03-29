package id.bluebird.mall.officer.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.bluebird.mall.officer.ui.home.queue_tab.FragmentType
import id.bluebird.mall.officer.ui.home.queue_tab.QueueFragment

class SlideFragment(fragmentManager: Fragment) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            QueueFragment(FragmentType.WAITING)
        } else {
            QueueFragment(FragmentType.DELAY)
        }
    }
}