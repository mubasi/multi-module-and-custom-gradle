package id.bluebird.mall.login.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.mall.login.ErrorType
import id.bluebird.mall.login.R
import id.bluebird.mall.login.databinding.BottomErrorLoginBinding

internal class DialogError(private val errorType: ErrorType) :
    BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().let {
            val bottomDialog = BottomSheetDialog(it, R.style.SheetDialog)
            bottomDialog.window?.also { window ->
                window.setBackgroundDrawableResource(android.R.color.transparent)
            }
            val binding = BottomErrorLoginBinding.inflate(LayoutInflater.from(it))
            binding.apply {
                btnDialogErrorLogin.setOnClickListener {
                    bottomDialog.dismiss()
                }
                tvTitleDialogErrorLogin.text = getTitle(it)
                tvMessageDialogErrorLogin.text = getMessage(it)
            }
            bottomDialog.setContentView(binding.root)
            bottomDialog
        }
    }

    private fun getTitle(context: Context): String = when (errorType) {
        ErrorType.Unknown -> context.getString(R.string.not_found_title)
        ErrorType.UserNotFound -> context.getString(R.string.unknown_title)
    }

    private fun getMessage(context: Context): String = when (errorType) {
        ErrorType.Unknown -> context.getString(R.string.not_found_message)
        ErrorType.UserNotFound -> context.getString(R.string.unknown_message)
    }
}

