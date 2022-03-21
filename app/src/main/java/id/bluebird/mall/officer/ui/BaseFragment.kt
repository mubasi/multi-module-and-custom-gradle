package id.bluebird.mall.officer.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.utils.top_snack.TSnackbar

abstract class BaseFragment : Fragment() {

    private lateinit var mainBodyFromFragment: View

    fun updateMainBody(view: View) {
        mainBodyFromFragment = view
    }

    fun topSnackBar(message: String, textColor: Int?, background: Int?) {
        val snackbar: TSnackbar = TSnackbar
            .make(mainBodyFromFragment, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(background ?: R.color.tsnack_color)
        val text =
            snackbarView.findViewById<TextView>(R.id.snackbar_text)
        text.setTextColor(
            textColor ?: ContextCompat.getColor(
                mainBodyFromFragment.context,
                (android.R.color.white)
            )
        )
        snackbar.show()
    }

    fun topSnackBarError(message: String) {
        val snackbar: TSnackbar = TSnackbar
            .make(mainBodyFromFragment, message, TSnackbar.LENGTH_LONG)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundResource(R.drawable.bg_error_alert)
        val imageView = snackbarView.findViewById<ImageView>(R.id.snackbar_action)
        val text =
            snackbarView.findViewById<TextView>(R.id.snackbar_text)
        text.setTextColor(
            ContextCompat.getColor(
                mainBodyFromFragment.context,
                (android.R.color.holo_red_dark)
            )
        )
        imageView.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}