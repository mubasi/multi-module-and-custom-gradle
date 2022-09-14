package id.bluebird.vsm.feature.monitoring.tableview.holder

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.databinding.TableHeaderCellLayoutBinding
import id.bluebird.vsm.feature.monitoring.model.MonitoringColumnHeader

class ColumnHeaderViewHolder(private val binding: TableHeaderCellLayoutBinding): AbstractViewHolder(binding.root) {

    fun bind(item: MonitoringColumnHeader) {
        with(binding) {
            value = item.data
        }
    }
}