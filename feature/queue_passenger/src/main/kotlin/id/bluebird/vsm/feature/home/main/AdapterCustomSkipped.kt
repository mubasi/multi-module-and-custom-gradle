package id.bluebird.vsm.feature.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.home.databinding.ItemListTertundaBinding
import id.bluebird.vsm.feature.home.model.QueueReceiptCache

class AdapterCustomSkipped(
    private val mList: ArrayList<QueueReceiptCache>,
    private val queuePassengerViewModel: QueuePassengerViewModel
    ) : RecyclerView.Adapter<AdapterCustomSkipped.ViewHolder>() {

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
                    queuePassengerViewModel.prosesDeleteQueue(mList[position])
                }

                binding.buttonRestoreSkipped.setOnClickListener {
                    queuePassengerViewModel.prosesRestoreQueue(mList[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}