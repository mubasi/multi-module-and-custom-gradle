package id.bluebird.vsm.feature.monitoring

import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import id.bluebird.vsm.feature.monitoring.edit_buffer.EditBufferViewModel
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel

object DataBinding {
    @JvmStatic
    @BindingAdapter("minusValue")
    fun setMinusButtonEnable(view: AppCompatImageButton, value: String) {
        val number = value.ifBlank { "0" }.toInt()
        view.isEnabled = number > EditBufferViewModel.MINIMUM_BUFFER
    }

    @JvmStatic
    @BindingAdapter("bindingIsDesc", "bindingStatusSort", "bindingVm")
    fun setVisibleIconOrder(view: AppCompatImageButton, isDesc : Boolean, statusSort: MonitoringViewModel.ActiveSort, viewModel : MonitoringViewModel) {
        view.isVisible = statusSort == viewModel.activeColumnSort.value
        view.setImageResource(
            if(isDesc) {
                R.drawable.ic_arrow_up_white
            } else {
                R.drawable.ic_arrow_down_white
            }
        )
    }
}