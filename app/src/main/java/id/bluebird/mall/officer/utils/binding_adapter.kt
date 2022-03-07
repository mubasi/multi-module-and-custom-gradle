package id.bluebird.mall.officer.utils

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.databinding.BindingAdapter
import id.bluebird.mall.officer.R

object binding_adapter {
    @JvmStatic
    @BindingAdapter("setCursorLastPosition")
    fun setCursorPosition(editText: EditText, text: String?) {
        if (text != null && text.isNotEmpty()) {
            editText.setSelection(text.length)
        }
    }

    @JvmStatic
    @BindingAdapter("show")
    fun setVisibility(view: View, bool: Boolean?) {
        view.visibility = if (bool == false) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("username_bind", "passwordBind")
    fun setLoginButtonDisplay(button: Button, username: String?, password: String?) {
        if (username == null || username.isEmpty()
            || password == null || password.isEmpty()
        ) {
            button.isEnabled = false
            button.background = button.context.getDrawable(R.drawable.bg_button_disable_login)
        } else {
            button.isEnabled = true
            button.background = button.context.getDrawable(R.drawable.bg_button_login)
        }
    }
}