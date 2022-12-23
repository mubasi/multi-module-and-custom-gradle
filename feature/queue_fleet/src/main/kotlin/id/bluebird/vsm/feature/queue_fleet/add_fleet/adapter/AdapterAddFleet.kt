package id.bluebird.vsm.feature.queue_fleet.add_fleet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.vsm.feature.queue_fleet.databinding.ItemAddFleetBinding


class AdapterAddFleet(private val viewModel: AddFleetViewModel) :
    ListAdapter<String, AdapterAddFleet.ViewHolder>(AddFleetDiffUtil()) {

    class ViewHolder(val binding: ItemAddFleetBinding, val viewModel: AddFleetViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fleetNumber: String, position: Int) {
            binding.apply {
                this.vm = viewModel
                this.fleetNumber = fleetNumber
                this.position = position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAddFleetBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_add_fleet,
            parent,
            false
        )
        return ViewHolder(binding = binding, viewModel = this.viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }
}