package id.bluebird.vsm.feature.monitoring.tableview.holder

import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.TableCellLayoutBufferBinding
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringCell
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel

class CellBufferViewHolder(private val binding: TableCellLayoutBufferBinding): AbstractViewHolder(binding.root) {

    fun bind(item: MonitoringCell, viewModel: MonitoringViewModel) {
        with(binding){
            vm = viewModel
            value = item.data
            if (item.obj is MonitoringModel)
                model = item.obj
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