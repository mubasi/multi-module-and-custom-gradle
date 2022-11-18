package id.bluebird.vsm.core.ui

import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import id.bluebird.vsm.core.GeneralError
import id.bluebird.vsm.core.R
import id.bluebird.vsm.core.utils.top_snack.TSnackbar

abstract class FragmentBase : Fragment() {

    private lateinit var mainBodyFromFragment: View

    fun updateMainBody(view: View) {
        mainBodyFromFragment = view
    }

    protected fun topSnackBar(message: SpannableString, background: Int?) {
        val snackbar: TSnackbar = TSnackbar
            .make(mainBodyFromFragment, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(background ?: R.color.tsnack_color)
        snackbarView.findViewById<ImageView>(R.id.snackbar_image).visibility = View.GONE
        snackbarView.findViewById<ImageView>(R.id.snackbar_action).visibility = View.GONE
        snackbar.show()
    }

    fun topSnackBar(view: ViewGroup, message: String, textColor: Int?, background: Int?) {
        val snackbar: TSnackbar = TSnackbar
            .make(view, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(background ?: R.color.tsnack_color)
        val text =
            snackbarView.findViewById<TextView>(R.id.snackbar_text)
        text.setTextColor(
            textColor ?: ContextCompat.getColor(
                view.context,
                (android.R.color.white)
            )
        )
        snackbar.show()
    }


    protected fun topSnackBarError(message: String) {
//        val messageInIndo = ExceptionHandler.getTranslateErrorToIndonesia(
//            requireContext(),
//            message
//        )
//        val snackbar: TSnackbar = TSnackbar
//            .make(mainBodyFromFragment, messageInIndo, TSnackbar.LENGTH_LONG)
//        val snackbarView: View = snackbar.view
//        snackbarView.setBackgroundResource(R.color.error_color)
//        snackbarView.findViewById<ImageView>(R.id.snackbar_image).visibility = View.GONE
//        snackbarView.findViewById<ImageView>(R.id.snackbar_action).visibility = View.GONE
//        snackbar.show()
    }

//    protected fun topSnackBarSuccess(message: SpannableString) {
//        topSnackBar(message, R.color.success_color)
//    }
//
//    protected fun topSnackBarDelay(message: SpannableString) {
//        topSnackBar(message, R.color.rating_color)
//    }

    protected fun generalError(generalError: GeneralError) {
        when (generalError) {
            is GeneralError.NotFound -> {
                topSnackBarError(generalError.message)
            }
            is GeneralError.NullPointerException -> {
                topSnackBarError(generalError.message)
            }
            is GeneralError.UnAuthorize -> {
                topSnackBarError(generalError.message)
            }
            is GeneralError.Unknown -> {
                topSnackBarError(generalError.message)
            }
        }
    }
}