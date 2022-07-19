package id.bluebird.mall.core.extensions

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object ViewExtensions {
    @JvmStatic
    @BindingAdapter("bindVisibility")
    fun View.visibility(boolean: Boolean) {
        this.isVisible = boolean
    }
}