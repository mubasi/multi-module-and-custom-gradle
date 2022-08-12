package id.bluebird.mall.feature_monitoring.model

import androidx.annotation.Keep
import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel

@Keep
open class MonitoringCell(
    val data: String,
    val obj: Any,
    val rowIndex: Int,
    val columnIndex: Int,
): ISortableModel, IFilterableModel {
    override fun getId(): String = data

    override fun getContent(): Any = data

    override fun getFilterableKeyword(): String = data
}
