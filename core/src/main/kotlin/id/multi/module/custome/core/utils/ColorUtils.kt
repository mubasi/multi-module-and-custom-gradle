package id.multi.module.custome.core.utils

class ColorUtils {
    companion object {
        fun getColor(isWithPassenger : Boolean, isStatusArrived : Boolean) : Int? {
            return if (isWithPassenger || !isStatusArrived) null else android.R.color.black
        }
    }
}