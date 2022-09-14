package id.bluebird.vsm.core.utils

import android.view.View
import androidx.databinding.BindingAdapter


object BindingAdapterCore {
    @JvmStatic
    @BindingAdapter("app:show")
    fun setVisibility(view: View, bool: Boolean?) {
        view.visibility = if (bool == false) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:enable")
    fun setAppEnable(view: View, bool: Boolean?) {
        view.isClickable = bool == true
    }

}