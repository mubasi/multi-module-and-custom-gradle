package id.bluebird.mall.feature_user_management.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.databinding.ActionBottomViewBinding
import id.bluebird.mall.feature_user_management.databinding.ErrorViewBinding
import id.bluebird.mall.feature_user_management.utils.top_snack.TSnackbar


object DialogUtil {
    fun showErrorDialog(context: Context, title: String?, message: String): BottomSheetDialog {
        val bottomDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.error_view, null)
        bottomDialog.setContentView(view)
        bottomDialog.window?.also {
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val binding = DataBindingUtil.bind<ErrorViewBinding>(view)
        binding?.tvMessageError?.text = message
        binding?.tvTitleError?.text = title ?: "Bermasalah"
        binding?.btnSubmitError
            ?.setOnClickListener { bottomDialog.dismiss() }
        bottomDialog.show()
        return bottomDialog
    }

    fun topSnackBar(view: View, message: String, textColor: Int?, background: Int?) {
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

    fun actionDialogUser(
        context: Context, title: String?, message: String, user: String,
        callback: () -> Unit
    ) {
        val messageWithUser = "$message \"$user\"?"
        showActionDialog(false, context, title, messageWithUser) {
            callback()
        }
    }

    private fun showActionDialog(
        isExit: Boolean,
        context: Context,
        title: String?,
        message: String,
        callback: () -> Unit
    ) {
        val bottomDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.action_bottom_view, null)
        bottomDialog.setContentView(view)
        bottomDialog.window?.also {
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val binding = DataBindingUtil.bind<ActionBottomViewBinding>(view)
        binding?.let {
            it.tvMessageActionBottom.text = message
            it.tvTitleActionBottom.text = title ?: "Bermasalah"
            if (isExit.not()) {
                it.btnYesBottom.text = context.getText(R.string.yes)
            }
            it.btnCancelBottom.setOnClickListener { bottomDialog.dismiss() }
            it.btnYesBottom
                .setOnClickListener {
                    callback()
                    bottomDialog.dismiss()
                }
        }
        bottomDialog.show()
    }
}