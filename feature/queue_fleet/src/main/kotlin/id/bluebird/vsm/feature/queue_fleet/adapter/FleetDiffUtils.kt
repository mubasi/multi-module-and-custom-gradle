package id.bluebird.vsm.feature.queue_fleet.adapter

import androidx.recyclerview.widget.DiffUtil
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

internal class FleetDiffUtils : DiffUtil.ItemCallback<FleetItem>() {
    override fun areItemsTheSame(oldItem: FleetItem, newItem: FleetItem): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FleetItem, newItem: FleetItem): Boolean =
        oldItem.name == newItem.name
}