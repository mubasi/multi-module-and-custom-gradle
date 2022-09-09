package id.bluebird.mall.feature.select_location.model

data class LocationModel(
    val id: Long,
    val name: String,
    var list: List<SubLocation>,
    var isExpanded: Boolean = false,
    var type: Int = PARENT
) {
    companion object {
        const val PARENT = 1
        const val CHILD = 2
    }
}