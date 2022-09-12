package id.bluebird.vsm.feature.monitoring

import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import id.bluebird.vsm.feature.monitoring.edit_buffer.EditBufferViewModel

object DataBinding {
    @JvmStatic
    @BindingAdapter("minusValue")
    fun setMinusButtonEnable(view: AppCompatImageButton, value: String) {
        val number = value.ifBlank { "0" }.toInt()
        view.isEnabled = number > EditBufferViewModel.MINIMUM_BUFFER
    }
}