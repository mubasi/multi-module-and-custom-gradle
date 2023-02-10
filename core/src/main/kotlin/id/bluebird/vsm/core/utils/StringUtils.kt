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
    }

}