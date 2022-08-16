package id.bluebird.mall.feature_user_management.utils

import android.content.res.ColorStateList
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.search_location.SearchLocationViewModel
import id.bluebird.mall.feature_user_management.search_location.model.Location

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("roleLiveDataSize", "isRoleSelected", "countSubAssignLocation")
    fun setButtonCreateUser(
        appCompatButton: AppCompatButton,
        roleLiveDataSize: Boolean?,
        isRoleSelected: Boolean?,
        countSubAssignLocation: Boolean?
    ) {
        with(appCompatButton) {
            if (roleLiveDataSize == true && isRoleSelected == true && countSubAssignLocation == true) {
                this.setBackgroundResource(
                    R.drawable.bg_confirm_meter_button
                )
                this.isEnabled = true
                this.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.white
                        )
                    )
                )
            } else {
                this.setBackgroundResource(R.drawable.bg_button_login_disable)
                this.isEnabled = false
                this.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.subFontColor
                        )
                    )
                )
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindingSubmitButton")
    fun setBindingSubmitButton(button: AppCompatButton, value: Location?) {
        button.apply {
            isEnabled = value != null
        }
    }

    @JvmStatic
    @BindingAdapter("locationItem", "viewModel")
    fun setCheckedLocation(view: AppCompatImageView, location: Location, viewModel: SearchLocationViewModel) {
        view.isVisible = viewModel.selectedLocation.value == location
    }
}