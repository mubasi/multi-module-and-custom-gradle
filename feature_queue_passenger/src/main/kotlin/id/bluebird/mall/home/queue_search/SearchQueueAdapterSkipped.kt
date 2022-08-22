package id.bluebird.mall.home.queue_search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.home.databinding.ItemListTertundaBinding
import id.bluebird.mall.home.model.QueueReceiptCache

class SearchQueueAdapterSkipped(
    private val mList: ArrayList<QueueReceiptCache>,
    private val queueSearchViewModel: QueueSearchViewModel
    ) : RecyclerView.Adapter<SearchQueueAdapterSkipped.ViewHolder>() {

    inner class ViewHolder(val binding: ItemListTertundaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListTertundaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(mList[position]){
                binding.numberValue.text = this.queueNumber
                binding.buttonDeleteSkipped.setOnClickListener {
                    queueSearchViewModel.prosesDeleteQueue(mList[position])
                }

                binding.buttonRestoreSkipped.setOnClickListener {
                    queueSearchViewModel.prosesRestoreQueue(mList[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}