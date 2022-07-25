package id.bluebird.mall.feature_queue_fleet.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.feature_queue_fleet.R
import id.bluebird.mall.feature_queue_fleet.databinding.ItemFleetBinding
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModel
import id.bluebird.mall.feature_queue_fleet.model.FleetItem

class FleetsAdapter(private val queueFleetViewModel: QueueFleetViewModel) :
    ListAdapter<FleetItem, FleetsAdapter.ViewHolder>(FleetDiffUtils()) {
    class ViewHolder(
        private val binding: ItemFleetBinding,
        private val viewModel: QueueFleetViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(fleetItem: FleetItem) {
            with(binding) {
                this.vm = viewModel
                fleet = fleetItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemFleetBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_fleet,
            parent,
            false
        )
        return ViewHolder(binding = binding, viewModel = queueFleetViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(holder.absoluteAdapterPosition)
        holder.setData(item)
    }
}