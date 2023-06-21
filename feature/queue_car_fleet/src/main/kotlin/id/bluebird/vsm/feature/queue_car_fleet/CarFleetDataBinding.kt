package id.bluebird.vsm.feature.queue_car_fleet

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.AddCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.main.QueueCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import id.bluebird.vsm.feature.queue_car_fleet.request_fleet.RequestCarFleetDialogViewModel
import id.bluebird.vsm.feature.queue_car_fleet.search_fleet.SearchCarFleetViewModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

object CarFleetDataBinding {

    @JvmStatic
    @BindingAdapter("valueCheckList", "bindingVm")
    fun showCheckList(imageView: AppCompatImageView, value: String, vm: AddCarFleetViewModel) {
        imageView.isVisible = vm.selectedFleetNumber.value == value
    }

    @JvmStatic
    @BindingAdapter("bindFleetItem", "bindingVm")
    fun Button.buttonClick(value: CarFleetItem, viewModel: ViewModel?) {
        viewModel?.let {
            this.setOnClickListener {
                when(viewModel) {
                    is SearchCarFleetViewModel -> viewModel.departFleet(value)
                    is QueueCarFleetViewModel -> viewModel.requestDepart(value)
                }
            }
        }
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
                counter.toInt() > RequestCarFleetDialogViewModel.MINIMUM_COUNTER_VALUE
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

    @JvmStatic
    @BindingAdapter("mainText", "coloredText", "toColor")
    fun setColoredText(textView: TextView, text: String, coloredText: String, color: Int) {
        textView.text = setDifferentColor(text, coloredText, color)
    }

    private fun setDifferentColor(text: String, key: String, color: Int): Spannable {
        val spannableString = SpannableStringBuilder(text)
        if (!text.contains(key, true))
            return spannableString

        val startPos = text.indexOf(key)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            startPos,
            startPos.plus(key.length),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        return spannableString
    }

    @JvmStatic
    @BindingAdapter("app:setOnRefresh")
    fun setOnRefresh(view: SwipeRefreshLayout, runnable: (() -> Unit)?) {
        view.setOnRefreshListener{
            runnable?.invoke()
            view.isRefreshing = false
        }
    }
    @JvmStatic
    @BindingAdapter("colorTextPassenger", "statusPassenger")
    fun setColorTextPassenger(view: TextView, withPassenger : Boolean, statusPassenger : Boolean) {
        view.textColor = view.context.getColor(
            if(statusPassenger) setColorWithPassenger(withPassenger) else setColorWithoutPassenger(withPassenger)
        )
    }
    private fun setColorWithPassenger(status : Boolean) : Int {
        return if(status) R.color.white else R.color.gray_disable
    }
    private fun setColorWithoutPassenger(status : Boolean) : Int {
        return if(status) R.color.gray_disable else R.color.white
    }
    @JvmStatic
    @BindingAdapter("withPassenger", "stPassenger")
    fun setImagePassenger(view: AppCompatImageView, withPassenger : Boolean, stPassenger : Boolean) {
        view.setImageDrawable(
            AppCompatResources.getDrawable(
                view.context,
                if(stPassenger) setImageWitPassenger(withPassenger) else setImageWithoutPassenger(withPassenger)
            )
        )
    }
    private fun setImageWithoutPassenger(status : Boolean) : Int {
        return if(status) R.drawable.no_with_passenger_no_active else R.drawable.no_with_passenger_active
    }
    private fun setImageWitPassenger(status : Boolean) : Int {
        return if(status) R.drawable.with_passenger_active else R.drawable.with_passenger_no_active
    }

    @JvmStatic
    @BindingAdapter("borderPassenger", "passengerStatus")
    fun setBorderPassenger(view: CardView, borderPassenger : Boolean, passengerStatus : Boolean) {
         view.backgroundColor = view.context.getColor(
             if(passengerStatus) setBorderWithPassenger(borderPassenger) else setBorderWithoutPassenger(borderPassenger)
         )
    }
    private fun setBorderWithPassenger(status : Boolean) : Int {
        return if(status) R.color.primary_0 else R.color.white
    }
    private fun setBorderWithoutPassenger(status : Boolean) : Int {
        return if(status) R.color.white else R.color.primary_0
    }

}