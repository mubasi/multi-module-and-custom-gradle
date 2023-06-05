package id.bluebird.vsm.feature.airport_fleet.add_fleet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.ItemAddFleetNonApshBinding

class AdapterAddFleet(
    private val vm: AddFleetViewModelNonApsh
) :
    RecyclerView.Adapter<AdapterAddFleet.ViewHolder>() {
    private val items: MutableList<String> = mutableListOf()

    class ViewHolder(
        val vm: AddFleetViewModelNonApsh,
        val binding: ItemAddFleetNonApshBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(fleetNumber: String) {
            binding.fleetNumber = fleetNumber
            binding.vm = vm
        }
    }

    fun updateList(list: List<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAddFleetNonApshBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_add_fleet_non_apsh,
            parent,
            false
        )
        return ViewHolder(vm = this.vm, binding = binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    override fun getItemCount(): Int = items.size
}