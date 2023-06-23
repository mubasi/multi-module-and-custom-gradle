package id.bluebird.vsm.feature.monitoring.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import id.bluebird.vsm.feature.monitoring.R
import id.bluebird.vsm.feature.monitoring.databinding.TableHeaderCornerLayoutBinding
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringCell
import id.bluebird.vsm.feature.monitoring.model.MonitoringColumnHeader
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringRowHeader
import id.bluebird.vsm.feature.monitoring.tableview.holder.*

class AdapterMonitoringTable(private val viewModel: MonitoringViewModel): AbstractTableAdapter<MonitoringColumnHeader, MonitoringRowHeader, MonitoringCell>() {
    private val helper = MonitoringTableHelper()
    private var mBinding: TableHeaderCornerLayoutBinding? = null
    private val sortDataCorner = MonitoringViewModel.ActiveSort.LocationName

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return when(viewType) {
            MonitoringTableHelper.BUFFER_CELL -> CellBufferViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.table_cell_layout_buffer, parent, false))
            else -> CellViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.table_cell_layout, parent, false))
        }
    }

    fun updateCorner(activeSort: MonitoringViewModel.ActiveSort){
        mBinding?.columnHeaderButton?.isVisible = activeSort == MonitoringViewModel.ActiveSort.LocationName
    }

    fun setHeaderLabel(list: List<String>) {
        helper.setHeaderLabels(list)
    }

    fun setItem(data: List<MonitoringModel>) {
        helper.generateListForTable(data, viewModel.sortIsDesc)
        setAllItems(helper.columnHeaderList, helper.rowHeaderList, helper.cellList)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: MonitoringCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
       when(holder) {
           is CellBufferViewHolder -> cellItemModel?.let { holder.bind(it, viewModel) }
           is CellViewHolder -> cellItemModel?.let { holder.bind(it) }
       }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return ColumnHeaderViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.table_header_cell_layout, parent, false),
            viewModel
        )
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderHeaderItemModel: MonitoringColumnHeader?,
        columnPosition: Int
    ) {
        if (holder is ColumnHeaderViewHolder)
            columnHeaderHeaderItemModel?.let { holder.bind(it) }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return RowHeaderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.table_cell_corner_layout, parent, false))
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderHeaderItemModel: MonitoringRowHeader?,
        rowPosition: Int
    ) {
        if (holder is RowHeaderViewHolder)
            rowHeaderHeaderItemModel?.let { holder.bind(it) }
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        val binding: TableHeaderCornerLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.table_header_corner_layout, parent, false)
        mBinding = binding
        setConditionBinding(parent)
        return binding.root
    }

    private fun setConditionBinding(parent: ViewGroup) {
        mBinding?.value = parent.context.getString(R.string.location)
        mBinding?.actionSort?.setOnClickListener {
            setChangeCorner()
        }
    }

    private fun setChangeCorner() {
        viewModel.changeStatusOrder(sortDataCorner, viewModel.sortIsDesc)
        mBinding?.columnHeaderButton?.setImageResource(
            if(viewModel.sortIsDesc) {
                R.drawable.ic_arrow_up_white
            } else {
                R.drawable.ic_arrow_down_white
            }
        )
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return helper.getRowItemViewType(position)
    }

    override fun getCellItemViewType(position: Int): Int {
        return helper.getCellItemViewType(position)
    }
}