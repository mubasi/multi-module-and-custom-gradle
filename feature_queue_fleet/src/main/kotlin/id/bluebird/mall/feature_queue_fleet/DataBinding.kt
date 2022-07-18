package id.bluebird.mall.feature_queue_fleet

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel

object DataBinding {

    @JvmStatic
    @BindingAdapter("valueCheckList", "bindingVm")
    fun showCheckList(imageView: AppCompatImageView, value: String, vm: AddFleetViewModel) {
        imageView.isVisible = vm.selectedFleetNumber.value == value
    }

    @JvmStatic
    @BindingAdapter("bindingButtonSubmit")
    fun setButtonSubmit(button: AppCompatButton, value: String) {
        button.apply {
            isClickable = value.isNotBlank()
            background = if (value.isNotBlank()) {
                setTextColor(ContextCompat.getColor(context, R.color.white))
                ContextCompat.getDrawable(context, R.drawable.bg_submit)
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.gray_disable))
                ContextCompat.getDrawable(context, R.drawable.bg_button_disable)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindMinusButton")
    fun setMinusButton(minusButton: AppCompatImageButton, counter: String) {
        if (counter.isNotEmpty()) {
            val isEnable =
                counter.toInt() > RequestFleetDialogViewModel.MINIMUM_COUNTER_VALUE
            minusButton.isEnabled = isEnable
            setButtonDisplayProperty(button = minusButton, isEnable = isEnable)
        } else {
            minusButtonDisable(minusButton)
        }
    }

    private fun setButtonDisplayProperty(button: AppCompatImageButton, isEnable: Boolean) {
        if (isEnable) {
            minusButtonEnable(button)
        } else {
            minusButtonDisable(button)
        }
    }

    private fun minusButtonEnable(button: AppCompatImageButton) {
        button.setImageDrawable(
            AppCompatResources.getDrawable(
                button.context,
                R.drawable.ic_minus_enable
            )
        )
        button.setBackgroundResource(R.drawable.bg_request_add_fleet)
    }

    private fun minusButtonDisable(button: AppCompatImageButton) {
        button.setImageDrawable(
            AppCompatResources.getDrawable(
                button.context,
                R.drawable.ic_minus_disable
            )
        )
        button.setBackgroundResource(R.drawable.bg_button_disable)
    }
}