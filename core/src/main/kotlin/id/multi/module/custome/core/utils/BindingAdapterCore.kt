package id.multi.module.custome.core.utils

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import id.multi.module.custome.core.R


object BindingAdapterCore {
    @JvmStatic
    @BindingAdapter("buttonBackgroundFleetCancel")
    fun setButtonBackgroundFleetCancel(appCompatButton: AppCompatButton, id: Long?) {
        val tempId = id ?: 1L
        with(appCompatButton) {
            setBackgroundResource(setButtonBackgroundByFleeTypeCancel(tempId))
            setTextColor(getColorStateConfig(appCompatButton.context, tempId))
        }
    }

    @JvmStatic
    @BindingAdapter("buttonBackgroundFleet")
    fun setButtonBackgroundFleet(appCompatButton: AppCompatButton, id: Long?) {
        val tempId = id ?: 1L
        appCompatButton.setBackgroundResource(setButtonBackgroundByFleeType(tempId))
    }

    private fun setButtonBackgroundByFleeType(id: Long?): Int {
        return if (id == 1L) {
            R.drawable.bg_confirm_meter_button
        } else {
            R.drawable.bg_confirm_silver_button
        }
    }

    private fun setButtonBackgroundByFleeTypeCancel(id: Long?): Int {
        return if (id == 1L) {
            R.drawable.bg_cancel_meter_button
        } else {
            R.drawable.bg_cancel_silver_button
        }
    }

    private fun getColorStateConfig(context: Context, id: Long?): ColorStateList {
        return when (id) {
            1L -> getMeterColorStateList(context)
            else -> getSilverColorStateList(context)
        }
    }

    private fun getMeterColorStateList(context: Context): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ColorStateList.valueOf(context.resources.getColor(R.color.primary_color, null))
        } else {
            ColorStateList.valueOf(context.resources.getColor(R.color.primary_color))
        }
    }

    private fun getSilverColorStateList(context: Context): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ColorStateList.valueOf(context.resources.getColor(R.color.gray_tile, null))
        } else {
            ColorStateList.valueOf(context.resources.getColor(R.color.gray_tile))
        }
    }


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