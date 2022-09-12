package id.bluebird.vsm.feature.user_management.search_location.adapter

import androidx.recyclerview.widget.DiffUtil
import id.bluebird.vsm.feature.user_management.search_location.model.Location

class LocationDiffUtil: DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean = oldItem == newItem
}