package id.bluebird.vsm.fleet_non_apsh.main.model

import androidx.annotation.Keep

@Keep
data class CountCache(var stock: Long = 0, var request: Long = 0, var ritase: Long = 0, var requestToPerimeter: Long = 0)
