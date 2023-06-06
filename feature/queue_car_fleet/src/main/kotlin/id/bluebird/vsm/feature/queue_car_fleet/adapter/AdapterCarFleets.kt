package id.bluebird.vsm.feature.queue_car_fleet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.ItemCarFleetBinding
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

class AdapterCarFleets :
    ListAdapter<CarFleetItem, AdapterCarFleets.ViewHolder>(CarFleetDiffUtils()) {

    companion object {

        private const val TAG = "AdapterFleets"
    }

    class ViewHolder(
        private val binding: ItemCarFleetBinding,
        private val _vm: ViewModel?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(carFleetItem: CarFleetItem) {
            with(binding) {
                fleet = carFleetItem
                this.vm = _vm
            }
        }
    }

    private var _viewModel: ViewModel? = null

    fun initViewModel(viewModel: ViewModel) {
        _viewModel = viewModel
    }

    fun submitData(list: List<CarFleetItem>) {
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCarFleetBinding >(
            LayoutInflater.from(parent.context),
            R.layout.item_car_fleet,
            parent,
            false
        )
        return ViewHolder(binding = binding, _vm = _viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(holder.bindingAdapterPosition)
        holder.setData(item)
    }
}