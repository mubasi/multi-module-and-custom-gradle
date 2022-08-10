package id.bluebird.mall.feature_monitoring.tableview.holder

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.mall.feature_monitoring.databinding.TableColumnHeaderLayoutBinding
import id.bluebird.mall.feature_monitoring.databinding.TableHeaderCellLayoutBinding
import id.bluebird.mall.feature_monitoring.model.MonitoringColumnHeader

class ColumnHeaderViewHolder(private val binding: TableHeaderCellLayoutBinding): AbstractViewHolder(binding.root) {

    fun bind(item: MonitoringColumnHeader) {
        with(binding) {
            value = item.data
        }
    }
}