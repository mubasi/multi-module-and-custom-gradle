package id.bluebird.vsm.feature.home.queue_search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.home.databinding.ItemListSearchBinding
import id.bluebird.vsm.feature.home.model.QueueSearchCache

class AdapterSearchQueue() : RecyclerView.Adapter<AdapterSearchQueue.ViewHolder>() {

    private val dataList: MutableList<QueueSearchCache> = arrayListOf()

    inner class ViewHolder(val binding: ItemListSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    fun submitList(newData: List<QueueSearchCache>) {
        val initSize = dataList.size
        dataList.clear()
        notifyItemRangeRemoved(0, initSize)
        dataList.addAll(newData)
        notifyItemRangeInserted(0, dataList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.item = dataList[position]
        }
    }
}