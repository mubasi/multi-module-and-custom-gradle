package id.bluebird.mall.feature_queue_fleet.main.adapter

import androidx.recyclerview.widget.DiffUtil
import id.bluebird.mall.feature_queue_fleet.model.FleetItem

internal class FleetDiffUtils : DiffUtil.ItemCallback<FleetItem>() {
    override fun areItemsTheSame(oldItem: FleetItem, newItem: FleetItem): Boolean =
        oldItem.id == newItem.id && oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FleetItem, newItem: FleetItem): Boolean =
        oldItem.id == newItem.id && oldItem.name == newItem.name
}