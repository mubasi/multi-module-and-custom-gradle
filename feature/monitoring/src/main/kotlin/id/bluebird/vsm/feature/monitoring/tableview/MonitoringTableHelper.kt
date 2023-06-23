package id.bluebird.vsm.feature.monitoring.tableview

import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringCell
import id.bluebird.vsm.feature.monitoring.model.MonitoringColumnHeader
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel
import id.bluebird.vsm.feature.monitoring.model.MonitoringRowHeader

class MonitoringTableHelper {
    companion object {
        const val BUFFER_CELL = 1
        const val ODD_ROW = 1
        const val EVEN_ROW = 2
    }

    private var _columnHeaderList = listOf<MonitoringColumnHeader>()
    private var _rowHeaderList = listOf<MonitoringRowHeader>()
    private var _cellList = listOf<List<MonitoringCell>>()
    private var headerLabels = listOf<String>()

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
     * Total Antrean Armada
     * Total Antrean Penumpang
     * Request Armada
     * Buffer
     * */

    private fun createColumnHeaderList(isDesc : Boolean): List<MonitoringColumnHeader> {
        val result : ArrayList<MonitoringColumnHeader> =  ArrayList()
        headerLabels.forEachIndexed { index, s ->
            result.add(
                MonitoringColumnHeader(
                    s,
                    index,
                    setStatusOrder(index),
                    isDesc
                )
            )
        }
        return result
    }

    private fun setStatusOrder(index : Int) : MonitoringViewModel.ActiveSort {
        return when(index) {
            1 -> {
                MonitoringViewModel.ActiveSort.FleetPassenger
            }
            2 -> {
                MonitoringViewModel.ActiveSort.TotalRitase
            }
            3 -> {
                MonitoringViewModel.ActiveSort.TotalQueueFleet
            }
            4 -> {
                MonitoringViewModel.ActiveSort.TotalPassengerQueue
            }
            5 -> {
                MonitoringViewModel.ActiveSort.RequestFleet
            }
            6 -> {
                MonitoringViewModel.ActiveSort.Deposition
            }
            else -> {
                MonitoringViewModel.ActiveSort.FleetNumber
            }
        }
    }


    fun setHeaderLabels(list: List<String>) {
        this.headerLabels = list
    }

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
            result.add(MonitoringRowHeader(value.locationName, value.subLocationName, index, value.subLocationId.toInt()))
        }
        return result
    }

    fun generateListForTable(monitoringList: List<MonitoringModel>, isDesc : Boolean) {
        _columnHeaderList = createColumnHeaderList(isDesc)
        _rowHeaderList = createRowHeader(monitoringList)
        _cellList = createCells(monitoringList)
    }
}