package id.bluebird.vsm.feature.select_location.adapter.airport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.select_location.R
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.select_location.databinding.ItemAirportBinding
import id.bluebird.vsm.feature.select_location.model.SubLocationModelCache

class AdapterAirport(
    private val _selectionVm: SelectLocationViewModel
) : RecyclerView.Adapter<AirportViewHolder>() {
    private val data = mutableListOf<SubLocationModelCache>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportViewHolder {
        val binding = DataBindingUtil.inflate<ItemAirportBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_airport,
            parent,
            false
        )
        return AirportViewHolder(_selectionVm, binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AirportViewHolder, position: Int) {
        holder.setData(data[position])
    }

    fun submitList(newData: List<SubLocationModelCache>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

}