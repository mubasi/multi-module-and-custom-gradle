package id.bluebird.vsm.feature.select_location.adapter.outlet

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.select_location.databinding.ItemChildLocationBinding
import id.bluebird.vsm.feature.select_location.model.LocationModel

class ChildViewHolder(val _vm: SelectLocationViewModel, val binding: ItemChildLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(locationModel: LocationModel) {
        with(binding) {
            this.subLocation = locationModel.list.first()
            this.vm = _vm
        }
    }
}