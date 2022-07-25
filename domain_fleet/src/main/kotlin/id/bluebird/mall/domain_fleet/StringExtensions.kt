package id.bluebird.mall.domain_fleet

object StringExtensions {
    internal fun String?.getItemPerPage(): Int = when {
        this == null || this.isBlank() -> 20
        this.length < 5 -> 30
        this.length < 3 -> 40
        else -> 10
    }
}