package id.bluebird.mall.officer.utils

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.GeneralError
import id.bluebird.mall.officer.common.network.model.ErrorModel
import okhttp3.ResponseBody
import javax.net.ssl.HttpsURLConnection


class ExceptionHandler {
    companion object {
        const val RESPONSE_BODY_IS_NULL = "body_null"
        const val OFFICER_NOT_FOUND = "officer not found"

        private const val UNKNOWN = "unknown"

        fun getTranslateErrorToIndonesia(context: Context, e: String): String {
            return when (e) {
                OFFICER_NOT_FOUND -> context.getString(R.string.user_not_found)
                else -> context.getString(R.string.error_is_unknown)
            }
        }

        fun generateExceptionCode(code: Int?, errorBody: ResponseBody?): GeneralError {
            if (code == null) {
                return GeneralError.Unknown(UNKNOWN)
            }
            val message = convertErrorBody(errorBody)
            return when (code) {
                HttpsURLConnection.HTTP_NOT_FOUND -> {
                    GeneralError.NotFound(message)
                }
                else -> GeneralError.Unknown(message)
            }
        }

        private fun convertErrorBody(errorBody: ResponseBody?): String {
            if (errorBody == null) {
                return UNKNOWN
            }
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<ErrorModel> = moshi.adapter(ErrorModel::class.java)
            return jsonAdapter.fromJson(errorBody.string())?.message ?: UNKNOWN
        }
    }
}