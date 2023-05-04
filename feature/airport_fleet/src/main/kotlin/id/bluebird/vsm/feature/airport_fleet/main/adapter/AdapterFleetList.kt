package id.bluebird.vsm.feature.airport_fleet.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.ItemCarFleetBinding
import id.bluebird.vsm.feature.airport_fleet.databinding.ViewLoadingPagesBinding
import id.bluebird.vsm.feature.airport_fleet.main.FleetNonApshViewModel
import id.bluebird.vsm.feature.airport_fleet.main.model.FleetItemCar
import id.bluebird.vsm.feature.airport_fleet.main.model.STATUS

class AdapterFleetList(
    private val viewModel: FleetNonApshViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    companion object {
        private const val LOADING_TYPE = 0
        private const val VIEW_TYPE = 1
        private const val LOADING_MORE = -1000L
    }

    private val models: MutableList<FleetItemCar> = ArrayList()
    private var tempModels: MutableList<FleetItemCar> = ArrayList()
    private var selectedStatus: String = ""
    private var tempChar: CharSequence = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == LOADING_TYPE) {
            val binding = DataBindingUtil.inflate<ViewLoadingPagesBinding>(
                inflater,
                R.layout.view_loading_pages,
                parent,
                false
            )
            LoadingHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemCarFleetBinding>(
                inflater,
                R.layout.item_car_fleet,
                parent,
                false
            )
            ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = tempModels[position]
        holder.setIsRecyclable(false)
        if (holder is ViewHolder) {
            holder.setData(viewModel, model, position)
        }
    }

    override fun getItemCount(): Int = tempModels.size

    override fun getItemViewType(position: Int): Int {
        return if (tempModels[position].id == LOADING_MORE) {
            LOADING_TYPE
        } else {
            VIEW_TYPE
        }
    }

    fun removeLoading() {
        if (tempModels.isNotEmpty() && tempModels.last().id == LOADING_MORE) {
            tempModels.removeLast()
            notifyItemRemoved(tempModels.lastIndex)
        }
    }

    fun addLoading() {
        if (tempModels.isNotEmpty() && tempModels.last().id != LOADING_MORE) {
            tempModels.add(FleetItemCar(id = LOADING_MORE))
            notifyItemInserted(tempModels.lastIndex)
        }
    }

    fun getLoadingPosition(): Int = tempModels.size - 1

    fun addNewItem(carAssignment: FleetItemCar, lastPosition: Int) {
        try {
            addCarToModelsByStatus(lastPosition, carAssignment)
            filterImpl.filter(tempChar)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun addCarToModelsByStatus(lastPosition: Int, carAssignment: FleetItemCar) {
        if (carAssignment.status == STATUS.ARRIVED.name) {
            models.add(lastPosition, carAssignment)
        } else {
            models.add(carAssignment)
        }
    }

    fun updateData(values: List<FleetItemCar>) {
        models.clear()
        models.addAll(values)
        filterImpl.filter(tempChar)
        notifyDataSetChanged()
    }

    fun updateCount(count: Int) {
        val isCountZero = count == 0
        if (isCountZero) {
            selectedStatus = ""
        }
        if (isCountZero || tempChar.isNotEmpty() || (count == 1)) {
            filterImpl.filter(tempChar)
        }
    }

    inner class ViewHolder(private val itemCarAssignmentBinding: ItemCarFleetBinding) :
        RecyclerView.ViewHolder(itemCarAssignmentBinding.root) {
        fun setData(
            perimeterViewModel: FleetNonApshViewModel,
            carAssignment: FleetItemCar?,
            position: Int
        ) {
            itemCarAssignmentBinding.fleetViewModelNonApsh = perimeterViewModel
            itemCarAssignmentBinding.fleet = carAssignment
            itemCarAssignmentBinding.cbCarAssignment.setOnCheckedChangeListener { _, bool ->
                if (carAssignment?.isSelected != bool) {
                    carAssignment!!.isSelected = bool
                    if (selectedStatus.isNotEmpty()) {
                        tempModels[position] = carAssignment
                        notifyItemChanged(position)
                    }
                    filterItemsByStatus(carAssignment.status)
                    selectedStatus = carAssignment.status
                    perimeterViewModel.setFleetSelected(carAssignment, carAssignment.isSelected)
                }
            }
        }
    }

    private fun filterItemsByStatus(status: String) {
        val temp: MutableList<FleetItemCar> = ArrayList()
        val position: MutableList<Int> = ArrayList()
        for (i in 0 until tempModels.size) {
            val item = tempModels[i]
            if (status != item.status && item.id != LOADING_MORE) {
                position.add(i)
                temp.add(item)
            }
        }
        if (position.isNotEmpty()) {
            tempModels.removeAll(temp)
        }
    }

    class LoadingHolder(viewLoadingBinding: ViewLoadingPagesBinding) :
        RecyclerView.ViewHolder(viewLoadingBinding.root)

    override fun getFilter(): Filter = filterImpl

    private val filterImpl = object : Filter() {
        override fun performFiltering(char: CharSequence?): FilterResults {
            tempChar = char ?: ""
            val result = FilterResults()
            if (tempChar.isNullOrEmpty() && selectedStatus == "") {
                result.values = models
                result.count = models.size
            } else {
                val map: MutableList<FleetItemCar> = ArrayList()
                models.forEach {
                    if (selectedStatus.isNotEmpty() && it.isSelected) {
                        map.add(it)
                        return@forEach
                    }
                    val itemCanAdd = checkIfItemCanAdd(it, char)
                    if (itemCanAdd != null) {
                        map.add(itemCanAdd)
                    }
                }
                result.values = map
                result.count = map.size
            }
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            tempModels.clear()
            if (p1 != null && p1.count > 0) {
                tempModels.addAll(p1.values as List<FleetItemCar>)
            }
            viewModel.updateListFleetState(tempChar.isNotEmpty() && tempModels.size == 0 && models.size != 0)
            notifyDataSetChanged()
        }
    }

    private fun checkIfItemCanAdd(it: FleetItemCar, char: CharSequence?): FleetItemCar? {
        return if (it.name.toLowerCase()
                .contains(
                    char.toString().toLowerCase()
                )
        ) {
            if (selectedStatus.isEmpty() || selectedStatus == it.status) {
                it
            } else
                null
        } else {
            null
        }

    }

}