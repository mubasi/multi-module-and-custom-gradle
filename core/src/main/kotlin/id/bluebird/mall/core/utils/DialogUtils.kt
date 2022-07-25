package id.bluebird.mall.core.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.bluebird.mall.core.R
import id.bluebird.mall.core.databinding.ErrorViewBinding

object DialogUtils {
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
}