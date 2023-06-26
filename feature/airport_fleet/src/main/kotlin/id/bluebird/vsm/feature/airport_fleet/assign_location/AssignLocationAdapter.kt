package id.bluebird.vsm.feature.airport_fleet.assign_location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.ItemLocationAssignListBinding
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignLocationModel

class AssignLocationAdapter(
    private val assignViewModel : AssignLocationViewModel
) :
    RecyclerView.Adapter<AssignLocationAdapter.ViewHolder>() {

    private val models: MutableList<AssignLocationModel> = ArrayList()
    private var selectedPosition: Int = -1

    class ViewHolder(val binding: ItemLocationAssignListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(
            perimeterViewModel: AssignLocationViewModel,
            model: AssignLocationModel,
            position: Int
        ) {
            binding.assignLocation = model
            binding.mainViewModel = perimeterViewModel
            binding.position = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLocationAssignListBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_location_assign_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.absoluteAdapterPosition
        val model = models[pos]
        holder.setData(assignViewModel, model, pos)
        holder.binding.radioAssignLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                assignViewModel.updateSelectedLocation(model)
                if (selectedPosition != -1 && pos != selectedPosition) {
                    models[selectedPosition].checked = false
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = pos
                assignViewModel.updateLocationToAssign(model)
            }
        }
    }

    fun updateData(values: List<AssignLocationModel>) {
        models.clear()
        models.addAll(values)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = models.size
}