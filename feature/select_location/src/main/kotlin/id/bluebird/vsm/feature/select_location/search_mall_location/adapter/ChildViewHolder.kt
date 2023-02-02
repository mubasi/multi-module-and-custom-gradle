package id.bluebird.vsm.feature.select_location.search_mall_location.adapter

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.databinding.ItemChildSearchLocationBinding
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.search_mall_location.SearchMallLocationViewModel

class ChildViewHolder(
    val _vm: SearchMallLocationViewModel,
    val binding: ItemChildSearchLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(locationModel: LocationModel) {
        with(binding) {
            this.subLocation = locationModel.list.first()
            this.vm = _vm
        }
    }
}