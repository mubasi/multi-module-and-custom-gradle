package id.bluebird.mall.feature_monitoring

import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import id.bluebird.mall.feature_monitoring.edit_buffer.EditBufferViewModel
import id.bluebird.mall.feature_monitoring.main.MonitoringViewModel
import id.bluebird.mall.feature_monitoring.model.MonitoringModel

object DataBinding {
    @JvmStatic
    @BindingAdapter("minusValue")
    fun setMinusButtonEnable(view: AppCompatImageButton, value: String) {
        val number = value.ifBlank { "0" }.toInt()
        view.isEnabled = number > EditBufferViewModel.MINIMUM_BUFFER
    }
}