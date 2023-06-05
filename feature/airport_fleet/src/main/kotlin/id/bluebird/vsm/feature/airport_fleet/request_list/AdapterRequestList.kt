package id.bluebird.vsm.feature.airport_fleet.request_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.ItemRequestListBinding

class AdapterRequestList : RecyclerView.Adapter<AdapterRequestList.ViewHolder>() {
    private val data: MutableList<FleetRequestDetail> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterRequestList.ViewHolder {
        val binding: ItemRequestListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_request_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterRequestList.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun submitList(newList: List<FleetRequestDetail>) {
        val initSize = data.size
        data.clear()
        notifyItemRangeRemoved(0, initSize)
        data.addAll(newList)
        notifyItemRangeInserted(0, data.size)
    }

    inner class ViewHolder(private val binding: ItemRequestListBinding ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FleetRequestDetail) {
            binding.apply {
                subLocationName = item.subLocationName
                requestCount = item.requestCount
            }
        }
    }
}