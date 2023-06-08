package id.bluebird.vsm.domain.fleet.model

import androidx.annotation.Keep

@Keep
data class CountResult(val stock: Long, val ritase: Long, val request: Long = -1, val deposition: Long = -1)
