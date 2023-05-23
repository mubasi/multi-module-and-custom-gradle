package id.bluebird.vsm.core.utils

import android.content.Context
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.bluebird.vsm.core.R
import id.bluebird.vsm.core.databinding.ActionCarAssignmentBinding
import id.bluebird.vsm.core.databinding.DialogProgressBinding
import id.bluebird.vsm.core.databinding.ErrorViewBinding
import id.bluebird.vsm.core.databinding.ResetBottomViewBinding

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

    fun progressDialog(
        context: Context,
        title: String?,
    ): BottomSheetDialog {
        val bottomDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
        bottomDialog.setContentView(view)
        bottomDialog.setCancelable(false)
        bottomDialog.window?.also {
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val binding = DataBindingUtil.bind<DialogProgressBinding>(view)
        binding?.tvTitleActionBottom?.text = title ?: context.getString(R.string.please_wait)
        bottomDialog.show()
        return bottomDialog
    }

    fun showSnackbar(view: View, context: Context, message: Spanned, background: Int?, textColor : Int?){
//        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
//        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)
//
//        layoutParams.gravity = Gravity.TOP
//        val tv = TypedValue()
//        val toolbarHeight = if (view.context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
//            TypedValue.complexToDimensionPixelSize(tv.data, view.context.resources.displayMetrics)
//        } else 0
//        layoutParams.setMargins(0, toolbarHeight, 0, 0)
//        snackbar.view.setPadding(0, 0, 0, 0)
//        snackbar.view.layoutParams = layoutParams
//        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
//        snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, colorId))
//        snackbar.show()

        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(-10, 120, -10, 0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.setTextColor(
            ContextCompat.getColor(
                context,
                textColor ?: R.color.white
            )
        )
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(
                context,
                background ?: R.color.tsnack_color
            )
        )
        snackbar.show()
    }

    fun showDialogAddFleet(
        context: Context,
        fleetNumber: String,
        callback: (isYes: Boolean) -> Unit
    ) {
        val bottomDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.reset_bottom_view, null)
        bottomDialog.setContentView(view)
        bottomDialog.window?.also {
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val binding = DataBindingUtil.bind<ResetBottomViewBinding>(view)
        binding?.let {
            it.tvMessageActionBottom.text = ""
            it.btnCancelBottom.text = context.getString(R.string.cancel)
            it.btnYesBottom.text = context.getString(R.string.add_fleet)
            it.tvTitleActionBottom.text = "Tambahkan armada $fleetNumber ?"
            it.btnCancelBottom.setOnClickListener {
                callback(false)
                bottomDialog.dismiss()
            }
            it.btnYesBottom.setOnClickListener {
                callback(true)
                bottomDialog.dismiss()
            }
        }
        bottomDialog.show()
    }


    fun showLeavingDialog(
        context: Context,
        isArrived: Boolean,
        callback: (isWithPassenger: Boolean?) -> Unit,
    ) {
        val bottomDialog = BottomSheetDialog(context, R.style.SheetDialog)
        bottomDialog.setCancelable(false)
        val view = LayoutInflater.from(context).inflate(R.layout.action_car_assignment, null)
        bottomDialog.setContentView(view)
        bottomDialog.window?.also {
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val binding = DataBindingUtil.bind<ActionCarAssignmentBinding>(view)
        binding?.status = isArrived
        val radioGroup = binding?.root?.findViewById<RadioGroup>(R.id.rg_car_go)
        binding?.btnCancelBottom
            ?.setOnClickListener {
                callback(null)
                bottomDialog.dismiss()
            }
        binding?.btnYesBottom
            ?.setOnClickListener {
                val temp = radioGroup?.checkedRadioButtonId
                callback(temp == R.id.rb_with_passenger)
                bottomDialog.dismiss()
            }
        bottomDialog.show()
    }
}