package id.bluebird.vsm.feature.select_location.search_mall_location.adapter

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.databinding.ItemParentSearchLocationBinding
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.search_mall_location.SearchMallLocationViewModel

class ParentViewHolder(val _vm: SearchMallLocationViewModel, val binding: ItemParentSearchLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(locationModel: LocationModel, position: Int) {
        with(binding) {
            location = locationModel
            vm = _vm
            pos = position
        }
    }
}