package id.bluebird.vsm.feature.monitoring.tableview.holder

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.databinding.TableCellLayoutBinding
import id.bluebird.vsm.feature.monitoring.model.MonitoringRowHeader

class RowHeaderViewHolder(private val binding: TableCellLayoutBinding): AbstractViewHolder(binding.root) {
    fun bind(item: MonitoringRowHeader) {
        with(binding ) {
            value = item.data
            isColored = item.rowIndex % 2 != 0
        }
    }
}