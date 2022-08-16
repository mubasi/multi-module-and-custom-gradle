package id.bluebird.mall.feature_user_management.search_location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.databinding.ItemLocationBinding
import id.bluebird.mall.feature_user_management.search_location.SearchLocationViewModel
import id.bluebird.mall.feature_user_management.search_location.model.Location

class SearchLocationAdapter(private val _vm: SearchLocationViewModel) :
    ListAdapter<Location, SearchLocationAdapter.ViewHolder>(LocationDiffUtil()) {
    inner class ViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Location, position: Int) {
            with(binding) {
                this.item = item
                this.position = position
                vm = _vm
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_location,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun setSelected(item: Location?) {
        if (item == null)
            currentList.forEachIndexed { index, it ->
                val lastValue = it.isSelected
                it.isSelected = false
                if (lastValue != it.isSelected)
                    notifyItemChanged(index)
            }
        else
            currentList.forEachIndexed { index, it ->
                val lastValue = it.isSelected
                it.isSelected = it == item
                if (lastValue != it.isSelected)
                    notifyItemChanged(index)
            }
    }
}