package id.bluebird.vsm.feature.monitoring.tableview.holder

import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.TableCellLayoutBinding
import id.bluebird.vsm.feature.monitoring.model.MonitoringCell

class CellViewHolder(private val binding: TableCellLayoutBinding): AbstractViewHolder(binding.root) {

    fun bind(item: MonitoringCell) {
        with(binding){
            value = item.data
            isColored = item.rowIndex % 2 != 0
        }
    }

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)

        if (selectionState == SelectionState.SELECTED) {
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.success_0))
        } else {
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
        }
    }
}