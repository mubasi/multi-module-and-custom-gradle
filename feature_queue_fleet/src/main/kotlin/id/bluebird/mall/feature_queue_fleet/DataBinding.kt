package id.bluebird.mall.feature_queue_fleet

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel

object DataBinding {

    @JvmStatic
    @BindingAdapter("bindButton")
    fun setButton(button: AppCompatImageButton, counter: Int) {
        val isEnable =
            counter > RequestFleetDialogViewModel.MINIMUM_COUNTER_VALUE
        button.isEnabled = isEnable
        if (isEnable) {
            button.setImageDrawable(
                AppCompatResources.getDrawable(
                    button.context,
                    R.drawable.ic_minus_enable
                )
            )
            button.setBackgroundResource(R.drawable.bg_request_add_fleet)
        } else {
            button.setImageDrawable(
                AppCompatResources.getDrawable(
                    button.context,
                    R.drawable.ic_minus_disable
                )
            )
            button.setBackgroundResource(R.drawable.bg_button_disable)
        }
    }
}