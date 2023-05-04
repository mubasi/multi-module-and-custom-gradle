package id.bluebird.vsm.feature.select_location.adapter.outlet

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.select_location.databinding.ItemParentLocationBinding
import id.bluebird.vsm.feature.select_location.model.LocationModel

class ParentViewHolder(val _vm: SelectLocationViewModel, val binding: ItemParentLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(locationModel: LocationModel, position: Int) {
        with(binding) {
            location = locationModel
            vm = _vm
            pos = position
        }
    }
}