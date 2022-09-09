package id.bluebird.mall.feature.select_location.adapter

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.feature.select_location.SelectLocationViewModel
import id.bluebird.mall.feature.select_location.databinding.ItemChildLocationBinding
import id.bluebird.mall.feature.select_location.databinding.ItemParentLocationBinding
import id.bluebird.mall.feature.select_location.model.LocationModel
import id.bluebird.mall.feature.select_location.model.SubLocation

class ChildViewHolder(val _vm: SelectLocationViewModel, val binding: ItemChildLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(locationModel: LocationModel) {
        with(binding) {
            this.subLocation = locationModel.list.first()
            this.vm = _vm
        }
    }
}