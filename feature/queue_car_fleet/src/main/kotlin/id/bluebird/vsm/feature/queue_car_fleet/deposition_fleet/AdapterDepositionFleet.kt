package id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.ItemDepositionFleetBinding
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

class AdapterDepositionFleet(
    private val viewModel: DepositionFleetViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listFleets: MutableList<CarFleetItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDepositionFleetBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_deposition_fleet,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun updateData(values: List<CarFleetItem>) {
        listFleets.clear()
        listFleets.addAll(values)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listFleets[position]
        holder.setIsRecyclable(false)
        if (holder is ViewHolder) {
            holder.setData(viewModel, model)
        }
    }

    override fun getItemCount(): Int = listFleets.size

    inner class ViewHolder(private val itemBinding : ItemDepositionFleetBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setData(
            viewModel: DepositionFleetViewModel,
            item: CarFleetItem,
        ) {
            itemBinding.fleet = item
            itemBinding.vm = viewModel
        }
    }

}