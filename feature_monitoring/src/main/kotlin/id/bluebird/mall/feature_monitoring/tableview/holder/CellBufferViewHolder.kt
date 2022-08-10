package id.bluebird.mall.feature_monitoring.tableview.holder

import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.mall.feature_monitoring.R
import id.bluebird.mall.feature_monitoring.databinding.TableCellLayoutBinding
import id.bluebird.mall.feature_monitoring.databinding.TableCellLayoutBufferBinding
import id.bluebird.mall.feature_monitoring.model.MonitoringCell

class CellBufferViewHolder(private val binding: TableCellLayoutBufferBinding): AbstractViewHolder(binding.root) {

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