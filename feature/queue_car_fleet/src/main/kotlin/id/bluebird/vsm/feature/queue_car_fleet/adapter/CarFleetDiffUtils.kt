package id.bluebird.vsm.feature.queue_car_fleet.adapter

import androidx.recyclerview.widget.DiffUtil
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

internal class CarFleetDiffUtils : DiffUtil.ItemCallback<CarFleetItem>() {
    override fun areItemsTheSame(oldItem: CarFleetItem, newItem: CarFleetItem): Boolean =
        oldItem.name == newItem.name && oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CarFleetItem, newItem: CarFleetItem): Boolean =
        oldItem.name == newItem.name && oldItem.id == newItem.id
}