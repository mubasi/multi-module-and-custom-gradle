package id.bluebird.vsm.feature.airport_fleet.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.databinding.CounterBarPagesBinding

class CounterBarPages @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {

        @BindingAdapter("app:onClickAdditionalDataFunction")
        @JvmStatic
        fun setOnClickAdditionalData(view: CounterBarPages, onDataClick: OnClickListener) {
            view.binding.additionalData.setOnClickListener(onDataClick)
        }

        @BindingAdapter("app:onClickPlusFunction")
        @JvmStatic
        fun setOnClickPlus(view: CounterBarPages, onPlusClick: OnClickListener) {
            view.binding.btnPlus.setOnClickListener(onPlusClick)
        }

    }

    var binding: CounterBarPagesBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.counter_bar_pages,
        this,
        true
    )
    private var forceShowAdditionalLocation = false
    private var requestCount: Int = -1
        set(value) {
            field = value
            setHighestRequestData(field, requestLocation, isPrimary = true)
        }
    private var requestLocation: String = ""
        set(value) {
            field = value
            setHighestRequestData(requestCount, field, isPrimary = true)
        }
    private var secondRequestCount: Int = -1
        set(value) {
            field = value
            setHighestRequestData(field, secondRequestLocation, isPrimary = false)
        }
    private var secondRequestLocation: String = ""
        set(value) {
            field = value
            setHighestRequestData(secondRequestCount, field, isPrimary = false)
        }

    init {
        attrs?.let { attributeSet ->
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.CounterBarView)

            typedArray.getString(R.styleable.CounterBarView_titleText)?.let {
                binding.tvTitle.text = it
            }

            if (typedArray.hasValue(R.styleable.CounterBarView_counterStyle)) {
                setCounterStyle(typedArray.getInt(R.styleable.CounterBarView_counterStyle, 0))
            }

            setPlusIconVisible(
                typedArray.getBoolean(
                    R.styleable.CounterBarView_plusIconVisible,
                    false
                )
            )

            typedArray.getString(R.styleable.CounterBarView_plusIconText)?.let {
                binding.btnPlus.text = it
            }

            setAdditionalDataVisible(
                typedArray.getBoolean(
                    R.styleable.CounterBarView_additionalDataVisible,
                    false
                )
            )
            setSecondaryAdditionalDataVisible(
                typedArray.getBoolean(
                    R.styleable.CounterBarView_secondaryAdditionalDataVisible,
                    false
                )
            )
            forceShowAdditionalLocation = typedArray.getBoolean(
                R.styleable.CounterBarView_forceShowAdditionalDataLocation,
                false
            )
            val count = typedArray.getInt(R.styleable.CounterBarView_highestRequestCount, -1)
            val location =
                typedArray.getString(R.styleable.CounterBarView_highestRequestLocation) ?: ""
            setHighestRequestData(count, location, isPrimary = true)
            val count2 = typedArray.getInt(R.styleable.CounterBarView_secondHighestRequestCount, -1)
            val location2 =
                typedArray.getString(R.styleable.CounterBarView_secondHighestRequestLocation) ?: ""
            setHighestRequestData(count2, location2, isPrimary = false)

            binding.tvTitle.setTextColor(
                typedArray.getColor(
                    R.styleable.CounterBarView_titleColor,
                    ContextCompat.getColor(context, R.color.gray_tile)
                )
            )

            typedArray.recycle()
        }
    }

    /**
     * make sure the id same as attrs value
     */
    enum class STYLE(
        val id: Int,
        @ColorRes val barColor: Int,
        @ColorRes val counterColor: Int,
        @ColorRes val additionalTextColor: Int? = null,
        @ColorRes val additionalBgColor: Int? = null
    ) {
        ORANGE(id = 0, barColor = R.color.warning_2, counterColor = R.color.warning_color),
        GREEN(id = 1, barColor = R.color.success_1, counterColor = R.color.success_color, additionalTextColor = R.color.success_0, additionalBgColor = R.color.success_3),
        BLACK(id = 2, barColor = R.color.gray_tile, counterColor = R.color.gray_line),
        BLUE(id = 3, barColor = R.color.primary_color, counterColor = R.color.primary_color),
        BLUE_ALT(id = 4, barColor = R.color.primary_color_second, counterColor = R.color.primary_color),
        YELLOW(id = 5, barColor = R.color.rating_min2, counterColor = R.color.rating_0, additionalTextColor = R.color.rating_plus1, additionalBgColor = R.color.rating_min3),
    }

    private fun setCounterStyle(value: Int) {
        val types = STYLE.values()
        val type = types.firstOrNull { it.id == value } ?: return
        setTypeByEnum(type)
    }

    private fun setTypeByEnum(style: STYLE) {
        binding.viewColor.setBackgroundColor(ContextCompat.getColor(context, style.barColor))
        binding.tvCounter.setTextColor(ContextCompat.getColor(context, style.counterColor))
        if (style.additionalBgColor != null) {
            binding.bgAdditionalData.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    style.additionalBgColor
                )
            )
            binding.bgSecondAdditionalData.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    style.additionalBgColor
                )
            )
        } else {
            binding.additionalData.isVisible = false
            binding.secondaryAdditionalData.isVisible = false
        }

        style.additionalTextColor?.let {
            binding.tvAdditionalData.setTextColor(ContextCompat.getColor(context, it))
            binding.tvSecondAdditionalData.setTextColor(ContextCompat.getColor(context, it))
        }
    }

    private fun setHighestRequestData(count: Int, location: String, isPrimary: Boolean) {
        val text = when (count) {
            -1 -> context.getString(R.string.channel_description)
            0 -> {
                if (forceShowAdditionalLocation) {
                    context.getString(
                        R.string.request_bar_placeholder,
                        count.toString(),
                        location
                    )
                } else {
                    context.getString(R.string.request_zero_placeholder, count.toString())
                }
            }
            else -> context.getString(
                R.string.request_bar_placeholder,
                count.toString(),
                location
            )
        }
        if (isPrimary) {
            binding.tvAdditionalData.text = text
        } else {
            binding.tvSecondAdditionalData.text = text
        }
    }

    fun setCounterText(string: String) {
        binding.tvCounter.text = string
    }

    fun setHighestRequestCount(count: MutableLiveData<Int>) {
        requestCount = count.value ?: -1
    }

    fun setHighestRequestLocation(location: MutableLiveData<String>) {
        requestLocation = location.value ?: ""
    }

    fun setSecondHighestRequestCount(count: MutableLiveData<Int>) {
        secondRequestCount = count.value ?: -1
    }

    fun setSecondHighestRequestLocation(location: MutableLiveData<String>) {
        secondRequestLocation = location.value ?: ""
    }

    fun setPlusIconVisible(boolean: Boolean) {
        binding.btnPlus.isVisible = boolean
    }

    private fun setAdditionalDataVisible(boolean: Boolean) {
        binding.additionalData.isVisible = boolean
    }

    fun setSecondaryAdditionalDataVisible(boolean: Boolean) {
        binding.secondaryAdditionalData.isVisible = boolean
    }

    fun setTitleText(string: String) {
        binding.tvTitle.text = string
    }

    fun setForceShowAdditionalDataLocation(boolean: Boolean) {
        this.forceShowAdditionalLocation = boolean
    }
}