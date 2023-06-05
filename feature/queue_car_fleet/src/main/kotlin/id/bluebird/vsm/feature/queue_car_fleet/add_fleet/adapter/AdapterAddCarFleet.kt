package id.bluebird.vsm.feature.queue_car_fleet.add_fleet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.AddCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.databinding.ItemAddCarFleetBinding


class AdapterAddCarFleet(private val viewModel: AddCarFleetViewModel) :
    ListAdapter<String, AdapterAddCarFleet.ViewHolder>(AddCarFleetDiffUtil()) {

    class ViewHolder(val binding: ItemAddCarFleetBinding, val viewModel: AddCarFleetViewModel) :
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
        val binding = DataBindingUtil.inflate<ItemAddCarFleetBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_add_car_fleet,
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