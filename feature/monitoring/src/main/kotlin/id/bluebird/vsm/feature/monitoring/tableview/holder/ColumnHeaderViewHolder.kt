package id.bluebird.vsm.feature.monitoring.tableview.holder

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.databinding.TableHeaderCellLayoutBinding
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringColumnHeader

class ColumnHeaderViewHolder(
    private val binding: TableHeaderCellLayoutBinding,
    private val viewModel : MonitoringViewModel
): AbstractViewHolder(binding.root) {

    fun bind(item: MonitoringColumnHeader) {
        with(binding) {
            value = item.data
            statusOrder = item.order
            vm = viewModel
            isDesc = item.isDesc
            actionSort.setOnClickListener {
                viewModel.changeStatusOrder(item.order, item.isDesc)
            }
        }
    }

}