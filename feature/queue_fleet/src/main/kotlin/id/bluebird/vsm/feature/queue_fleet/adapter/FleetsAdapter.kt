package id.bluebird.vsm.feature.queue_fleet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.queue_fleet.R
import id.bluebird.vsm.feature.queue_fleet.databinding.ItemFleetBinding
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

class FleetsAdapter :
    ListAdapter<FleetItem, FleetsAdapter.ViewHolder>(FleetDiffUtils()) {


    class ViewHolder(
        private val binding: ItemFleetBinding,
        private val _vm: ViewModel?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(fleetItem: FleetItem) {
            with(binding) {
                fleet = fleetItem
                this.vm = _vm
            }
        }
    }

    private var _viewModel: ViewModel? = null

    fun initViewModel(viewModel: ViewModel) {
        _viewModel = viewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemFleetBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_fleet,
            parent,
            false
        )
        return ViewHolder(binding = binding, _vm = _viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(holder.absoluteAdapterPosition)
        holder.setData(item)
    }
}