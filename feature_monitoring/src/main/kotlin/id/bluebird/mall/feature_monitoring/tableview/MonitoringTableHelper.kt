package id.bluebird.mall.feature_monitoring.tableview

import id.bluebird.mall.feature_monitoring.model.MonitoringCell
import id.bluebird.mall.feature_monitoring.model.MonitoringColumnHeader
import id.bluebird.mall.feature_monitoring.model.MonitoringModel
import id.bluebird.mall.feature_monitoring.model.MonitoringRowHeader

class MonitoringTableHelper {
    companion object {
        const val BUFFER_CELL = 1
        const val ODD_ROW = 1
        const val EVEN_ROW = 2
    }

    private var _columnHeaderList = listOf<MonitoringColumnHeader>()
    private var _rowHeaderList = listOf<MonitoringRowHeader>()
    private var _cellList = listOf<List<MonitoringCell>>()

    val columnHeaderList: List<MonitoringColumnHeader> get() = _columnHeaderList
    val rowHeaderList: List<MonitoringRowHeader> get() = _rowHeaderList
    val cellList: List<List<MonitoringCell>> get() = _cellList

    fun getCellItemViewType(column: Int): Int =
        when (column) {
            6 -> BUFFER_CELL
            else -> 0
        }


    fun getRowItemViewType(position: Int): Int = if (position%2 == 0) EVEN_ROW else ODD_ROW

    /**
     * Lokasi
     * Total Antrian Armada
     * Total Antrian Penumpang
     * Request Armada
     * Buffer
     * */

    private fun createColumnHeaderList(): List<MonitoringColumnHeader> =
        listOf(
            MonitoringColumnHeader("Antrian Armada"),
            MonitoringColumnHeader("Antrian Penumpang"),
            MonitoringColumnHeader("Total Ritase"),
            MonitoringColumnHeader("Total Antrian Armada"),
            MonitoringColumnHeader("Total Antrian Penumpang"),
            MonitoringColumnHeader("Request Armada"),
            MonitoringColumnHeader("Buffer"),
        )

    private fun createCells(list: List<MonitoringModel>): List<List<MonitoringCell>> {
        val result: MutableList<MutableList<MonitoringCell>> = MutableList(list.size) { mutableListOf() }

        result.forEachIndexed { index, value ->
            val model = list[index]
            value.clear()
            value.add(MonitoringCell(model.fleetCount.toString(), model, index, 1))
            value.add(MonitoringCell(model.queueCount.toString(), model, index, 2))
            value.add(MonitoringCell(model.totalRitase.toString(), model, index, 2))
            value.add(MonitoringCell(model.totalFleetCount.toString(), model, index, 2))
            value.add(MonitoringCell(model.totalQueueCount.toString(), model, index, 2))
            value.add(MonitoringCell(model.fleetRequest.toString(), model, index, 3))
            value.add(MonitoringCell(model.buffer.toString(), model, index, 4))
        }

        return result
    }

    private fun createRowHeader(list: List<MonitoringModel>): List<MonitoringRowHeader> {
        val result = mutableListOf<MonitoringRowHeader>()
        list.forEachIndexed { index, value ->
            result.add(MonitoringRowHeader(value.locationName, index, value.subLocationId.toInt()))
        }

        return result
    }

    fun generateListForTable(monitoringList: List<MonitoringModel>) {
        _columnHeaderList = createColumnHeaderList()
        _rowHeaderList = createRowHeader(monitoringList)
        _cellList = createCells(monitoringList)
    }
}