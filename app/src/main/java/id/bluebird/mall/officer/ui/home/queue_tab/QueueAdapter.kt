package id.bluebird.mall.officer.ui.home.queue_tab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.databinding.ItemQueueBinding
import id.bluebird.mall.officer.ui.home.HomeViewModel
import id.bluebird.mall.officer.ui.home.model.QueueCache

class QueueAdapter(
    private val fragmentType: FragmentType,
    private val homeViewModel: HomeViewModel
) :
    RecyclerView.Adapter<QueueAdapter.ViewHolder>() {
    private val items: MutableList<QueueCache> by lazy {
        val items: MutableList<QueueCache> = ArrayList()
        items.add(QueueCache(1, isDelay = false, isCurrentQueue = true, isVisible = false))
        items
    }

    class ViewHolder(
        val binding: ItemQueueBinding, private val fragmentType: FragmentType,
        private val homeViewModel: HomeViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(item: QueueCache) {
            binding.vm = null
            binding.item = item
            binding.vm = homeViewModel
            binding.fragmentType = fragmentType
        }
    }

    fun setItems(newItems: List<QueueCache>) {
        items.clear()
        if (newItems.isEmpty()) {
            items.add(QueueCache(1, isDelay = false, isCurrentQueue = true, isVisible = false))
        } else {
            items.addAll(newItems)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_queue,
                parent,
                false
            ),
            fragmentType,
            homeViewModel
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.set(items[position])
    }

    override fun getItemCount(): Int = items.size
}