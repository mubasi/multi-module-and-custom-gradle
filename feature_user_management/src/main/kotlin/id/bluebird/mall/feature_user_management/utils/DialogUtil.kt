package id.bluebird.mall.feature_user_management.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.databinding.ActionBottomViewBinding


object DialogUtil {

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