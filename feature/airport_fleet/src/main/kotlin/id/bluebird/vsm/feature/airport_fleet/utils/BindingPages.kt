package id.bluebird.vsm.feature.airport_fleet.utils

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.feature.airport_fleet.main.model.FleetItemCar
import id.bluebird.vsm.feature.airport_fleet.main.model.STATUS

object BindingPages {

    private var mFleetType: Long = 1L

    @JvmStatic
    @BindingAdapter("indeterminateTintConfig")
    fun setIndeterminateTintConfig(progressBar: ProgressBar, fleetTypeId: Long?) {
        progressBar.indeterminateTintList =
            getColorStateConfig(progressBar.context, fleetTypeId ?: mFleetType)
    }

    @JvmStatic
    @BindingAdapter("withWithout")
    fun setTextForNonTerminal(view: View, subLocationName: String) {
        (view as TextView).text = when (subLocationName.toLowerCase()) {
            "with passenger" -> view.context.getString(R.string.with_passenger)
            "without passenger" -> view.context.getString(R.string.without_passenger)
            else -> subLocationName
        }
    }

    @JvmStatic
    @BindingAdapter("radioButtonTint")
    fun setRadioButton(radioButton: RadioButton, long: Long?) {
        radioButton.buttonTintList = getColorStateConfig(radioButton.context, long ?: 2L)
    }

    @JvmStatic
    @BindingAdapter("statusTitle")
    fun setStatusTitle(textView: TextView, emptyType: EmptyType?) {
        emptyType?.let {
            textView.text = when (emptyType) {
                EmptyType.Perimeter -> {
                    textView.context.getString(R.string.fleet_stock_empty)
                }
                EmptyType.Terminal -> {
                    textView.context.getString(R.string.fleet_is_not_available)
                }
                EmptyType.FilterFleet -> {
                    textView.context.getString(R.string.fleet_not_found)
                }
                else -> {
                    textView.context.getString(R.string.fleet_is_not_available)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("statusMessage")
    fun setStatusMessage(textView: TextView, emptyType: EmptyType?) {
        emptyType?.let {
            textView.text = when (emptyType) {
                EmptyType.Perimeter -> {
                    textView.context.getString(R.string.please_add_fleet_message)
                }
                EmptyType.Terminal -> {
                    textView.context.getString(R.string.please_request_fleet_message)
                }
                EmptyType.FilterFleet -> {
                    textView.context.getString(R.string.fleet_not_found_message)
                }
                else -> {
                    textView.context.getString(R.string.please_request_fleet_message)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("fleetStatus")
    fun setTextStatus(view: View, status: String?) {
        if (status != null) {
            setTextByStatus(view as TextView, status)
        }
    }

    @JvmStatic
    @BindingAdapter("fleetCar")
    fun setText(view: AppCompatButton, carAssignment: AssignmentCarCache?) {
        if (carAssignment != null) {
            val textView = view as TextView
            setTextByStatus(textView, carAssignment.status)
        }
    }

    private fun setTextByStatus(textView: TextView, status: String) {
        when (status) {
            STATUS.OTW.name -> textView.text = textView.context.getString(R.string.arrived)
            STATUS.ARRIVED.name -> textView.text =
                textView.context.getString(R.string.leave)
            else -> textView.text = textView.context.getString(R.string.assign)
        }
    }

    private fun setButtonBackgroundByFleeType(id: Long?): Int {
        return if (id == 1L) {
            R.drawable.bg_confirm_meter_button
        } else {
            R.drawable.bg_confirm_silver_button
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
    @BindingAdapter("buttonBackgroundFleet")
    fun setButtonBackgroundFleet(appCompatButton: AppCompatButton, id: Long?) {
        val tempId = id ?: mFleetType
        appCompatButton.setBackgroundResource(setButtonBackgroundByFleeType(tempId))
    }

    @JvmStatic
    @BindingAdapter("buttonBackgroundFleetCancel")
    fun setButtonBackgroundFleetCancel(appCompatButton: AppCompatButton, id: Long?) {
        val tempId = id ?: mFleetType
        with(appCompatButton) {
            setBackgroundResource(setButtonBackgroundByFleeTypeCancel(tempId))
            setTextColor(getColorStateConfig(appCompatButton.context, tempId))
        }
    }

    private fun setButtonBackgroundByFleeTypeCancel(id: Long?): Int {
        return if (id == 1L) {
            R.drawable.bg_cancel_meter_button
        } else {
            R.drawable.bg_cancel_silver_button
        }
    }

    @JvmStatic
    @BindingAdapter("minimumRequest")
    fun setMinimumRequest(button: Button, requestCounter: String) {
        button.setBackgroundResource(
            if (requestCounter.toInt() > 1) {
                button.isEnabled = true
                if (mFleetType == 1L) {
                    R.drawable.ic_minus_request_meter
                } else {
                    R.drawable.ic_minus_request_silver
                }
            } else {
                button.isEnabled = false
                R.drawable.ic_minus_request_disable
            }
        )
    }

    @JvmStatic
    @BindingAdapter("requestAddCounter")
    fun setRequestAddCounter(button: Button, requestCounter: Long?) {
        button.setBackgroundResource(
            if (mFleetType == 1L) {
                R.drawable.ic_add_request_meter
            } else {
                R.drawable.ic_add_request_silver
            }
        )
    }

    @JvmStatic
    @BindingAdapter("buttonAssign", "enableAssign")
    fun setButtonAssign(
        button: AppCompatButton,
        id: Long?,
        enable: Boolean?
    ) {
        val tempId = id ?: 1L
        button.setBackgroundResource(
            if (enable == true
            ) {
                button.isEnabled = true
                setButtonBackgroundByFleeType(tempId)
            } else {
                button.isEnabled = false
                R.drawable.bg_button_login_disable
            }
        )
    }

}