package id.bluebird.mall.officer.ui

import android.text.SpannableString
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.GeneralError
import id.bluebird.mall.officer.utils.top_snack.TSnackbar

abstract class BaseFragment : Fragment() {

    private lateinit var mainBodyFromFragment: View
    fun updateMainBody(view: View) {
        mainBodyFromFragment = view
    }

    private fun topSnackBar(message: SpannableString, background: Int?) {
        val snackbar: TSnackbar = TSnackbar
            .make(mainBodyFromFragment, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(background ?: R.color.tsnack_color)
        snackbarView.findViewById<ImageView>(R.id.snackbar_image).visibility = View.GONE
        snackbarView.findViewById<ImageView>(R.id.snackbar_action).visibility = View.GONE
        snackbar.show()
    }

    protected fun topSnackBarError(message: String) {
        val snackbar: TSnackbar = TSnackbar
            .make(mainBodyFromFragment, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(R.color.error_color)
        snackbarView.findViewById<ImageView>(R.id.snackbar_image).visibility = View.GONE
        snackbarView.findViewById<ImageView>(R.id.snackbar_action).visibility = View.GONE
        snackbar.show()
    }

    protected fun topSnackBarSuccess(message: SpannableString) {
        topSnackBar(message, R.color.success_color)
    }

    protected fun topSnackBarDelay(message: SpannableString) {
        topSnackBar(message, R.color.rating_color)

    }

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