package id.bluebird.vsm.feature.user_management.utils

import android.content.Context
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.feature.user_management.R
import id.bluebird.vsm.feature.user_management.databinding.ActionBottomViewBinding


object DialogUtil {

    fun showSnackbar(view: View, message: Spanned, color: Int){
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(-10,160,-10,0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, color))
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