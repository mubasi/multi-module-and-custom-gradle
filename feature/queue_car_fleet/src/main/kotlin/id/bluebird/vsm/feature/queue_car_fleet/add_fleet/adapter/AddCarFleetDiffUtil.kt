package id.bluebird.vsm.feature.queue_car_fleet.add_fleet.adapter

import androidx.recyclerview.widget.DiffUtil

class AddCarFleetDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}