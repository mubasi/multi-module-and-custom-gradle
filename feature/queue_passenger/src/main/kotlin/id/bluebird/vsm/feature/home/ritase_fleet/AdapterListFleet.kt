package id.bluebird.vsm.feature.home.ritase_fleet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.ItemRitaseFleetBinding
import id.bluebird.vsm.feature.home.model.FleetItemList

class AdapterListFleet(
    private val _vm: RitaseFleetViewModel
) : RecyclerView.Adapter<AdapterListFleet.ViewHolder >()  {
    private var data: MutableList<FleetItemList> = arrayListOf()
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_ritase_fleet, parent, false),
            _vm
        )
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(
        private val binding: ItemRitaseFleetBinding, private val viewModel: RitaseFleetViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            fleetNumber : String,
            position: Int
        ) {
            binding.apply {
                this.vm = viewModel
                this.fleetNumber = fleetNumber
                this.position = position
            }
        }
    }

    private fun clearSelected(){
        if (selectedPosition >= 0 && data.size > 0) {
            val currentList = data.filter { it.isSelected }
            for (i in 0 .. currentList.size) {
                data[i].isSelected = false
            }
        }
        selectedPosition = -1
    }

    fun setData(list: List<FleetItemList>) {
        clearSelected()
        val initSize = data.size
        data.clear()
        notifyItemRangeRemoved(0, initSize)
        data = list.toMutableList()
        notifyItemRangeInserted(0, data.size)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position].name, position)
    }

}