package id.bluebird.mall.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.home.databinding.ItemListQueueBinding
import id.bluebird.mall.home.model.QueueReceiptCache

class CustomAdapterSkipped(private val mList: ArrayList<QueueReceiptCache>) : RecyclerView.Adapter<CustomAdapterSkipped.ViewHolder>() {


    inner class ViewHolder(val binding: ItemListQueueBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(mList[position]){
                binding.numberValue.text = this.queueNumber
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}