package id.bluebird.mall.feature_monitoring.tableview.holder

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.mall.feature_monitoring.databinding.TableCellLayoutBinding
import id.bluebird.mall.feature_monitoring.model.MonitoringRowHeader

class RowHeaderViewHolder(private val binding: TableCellLayoutBinding): AbstractViewHolder(binding.root) {
    fun bind(item: MonitoringRowHeader) {
        with(binding ) {
            value = item.data
            isColored = item.rowIndex % 2 != 0
        }
    }
}