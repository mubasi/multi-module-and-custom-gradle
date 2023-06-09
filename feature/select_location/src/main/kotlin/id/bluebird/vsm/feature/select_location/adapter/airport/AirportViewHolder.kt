package id.bluebird.vsm.feature.select_location.adapter.airport

import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.select_location.databinding.ItemAirportBinding
import id.bluebird.vsm.feature.select_location.model.SubLocationModelCache

class AirportViewHolder(
    val _vm: SelectLocationViewModel,
    private val _itemAirportBinding: ItemAirportBinding
) : RecyclerView.ViewHolder(_itemAirportBinding.root) {
    fun setData(subLocation: SubLocationModelCache) {
        with(_itemAirportBinding) {
            vm = _vm
            this.subLocation = subLocation
        }
    }
}