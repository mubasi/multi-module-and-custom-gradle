package id.bluebird.vsm.core.utils

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.bold
import id.bluebird.vsm.core.R
import kotlin.math.min

class StringUtils {

    companion object {
        fun getErrorMessage(title : String?,  message: String?, context: Context): Spanned {
            val title = title ?: context.getString(R.string.there_is_error)
            var msg = message ?: context.getString(R.string.please_try_again)

            if(msg.length > 50) {
                msg = msg.substring(0, min(msg.length, 50)) + " ..."
            }

            return SpannableStringBuilder()
                .bold { append(title) }
                .append(" ")
                .append(msg)
        }

        fun getMessage(
            context: Context,
            isNonTerminal: Boolean,
            isWithPassenger: Boolean,
            name : String
        ): String {
            return if (isNonTerminal) {
                context.getString(if (isWithPassenger) R.string.leave_with_passenger else R.string.leave_without_passenger)
            } else {
                " ${
                    context.getString(
                        R.string.car_success_assign_to
                    )
                } $name"
            }
        }


        fun getMessageRitase(
            context: Context,
            message : Any,
            isWithPassenger : Boolean,
            isStatusArrived : Boolean
        ) : String {
            val actionMessage = if (isStatusArrived) {
                context.getString(
                    if (isWithPassenger) R.string.leave_with_passenger else R.string.leave_without_passenger
                )
            } else {
                context.getString(R.string.confirm_fleet_arrived_success)
            }
            return "$message ${if (message is Int) context.getString(R.string.armada) else ""} $actionMessage"
        }
    }

}