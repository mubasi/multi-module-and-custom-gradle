package id.bluebird.mall.feature_queue_fleet

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel

object DataBinding {

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