package id.bluebird.vsm.feature.queue_fleet.add_fleet.adapter

import androidx.recyclerview.widget.DiffUtil

class AddFleetDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}