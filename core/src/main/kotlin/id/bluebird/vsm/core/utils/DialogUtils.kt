package id.bluebird.vsm.core.utils

import android.content.Context
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.core.R
import id.bluebird.vsm.core.databinding.ErrorViewBinding

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

    fun showSnackbar(view: View, message: Spanned, colorId: Int){
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        val tv = TypedValue()
        val toolbarHeight = if (view.context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, view.context.resources.displayMetrics)
        } else 0
        layoutParams.setMargins(0, toolbarHeight, 0, 0)
        snackbar.view.setPadding(0, 0, 0, 0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, colorId))
        snackbar.show()
    }
}