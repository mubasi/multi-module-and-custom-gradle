package id.bluebird.mall.feature.select_location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.feature.select_location.R
import id.bluebird.mall.feature.select_location.SelectLocationViewModel
import id.bluebird.mall.feature.select_location.databinding.ItemParentLocationBinding
import id.bluebird.mall.feature.select_location.model.LocationModel

class AdapterSelectLocation(private val _vm: SelectLocationViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<LocationModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemParentLocationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_parent_location,
            parent,
            false
        )
        return when (viewType) {
            LocationModel.CHILD -> ChildViewHolder(
                _vm,
                DataBindingUtil.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), R.layout.item_child_location, parent, false
                )
            )
            else -> ParentViewHolder(_vm, binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = data[position]
        when (holder) {
            is ChildViewHolder -> {
                holder.setData(row)
            }
            is ParentViewHolder -> {
                holder.setData(row, position)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].type

    fun expandOrCollapseParent(position: Int) {
        val parent = data[position]
        if (parent.isExpanded) {
            collapseParent(position)
        } else {
            expandParent(position)
        }
    }

    private fun expandParent(position: Int) {
        val currentLocationRow = data[position]
        val subLocations = currentLocationRow.list
        data[position].isExpanded = true
        var nextPosition = position
        notifyItemChanged(position)
        if (currentLocationRow.type == LocationModel.PARENT) {
            subLocations.forEach {
                val parentModel = currentLocationRow.copy()
                parentModel.type = LocationModel.CHILD
                parentModel.list = listOf(it)
                data.add(++nextPosition, parentModel)
            }
            notifyItemInserted(position + 1)
            notifyItemRangeChanged(position, data.size - position)
        }
    }

    private fun collapseParent(position: Int) {
        val currentLocationRow = data[position]
        val subLocations = currentLocationRow.list
        data[position].isExpanded = false
        notifyItemChanged(position)
        if (currentLocationRow.type == LocationModel.PARENT) {
            subLocations.forEach { _ ->
                data.removeAt(position + 1)
                notifyItemRemoved(position + 1)
            }
            notifyItemRangeChanged(position, data.size - position)
        }
    }

    fun submitList(newData: List<LocationModel>) {
        val initSize = data.size
        data.clear()
        notifyItemRangeRemoved(0, initSize)
        data.addAll(newData)
        notifyItemRangeInserted(0, data.size)
    }
}
