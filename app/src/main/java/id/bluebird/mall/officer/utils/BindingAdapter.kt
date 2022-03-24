package id.bluebird.mall.officer.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.ui.home.HomeViewModel

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:setCursorLastPosition")
    fun setCursorPosition(editText: EditText, text: String?) {
        if (text != null && text.isNotEmpty()) {
            editText.setSelection(text.length)
        }
    }

    @JvmStatic
    @BindingAdapter("app:show")
    fun setVisibility(view: View, bool: Boolean?) {
        view.visibility = if (bool == false) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:username", "app:password")
    fun setLoginButtonDisplay(button: Button, username: String?, password: String?) {
        if (username == null || username.isEmpty()
            || password == null || password.isEmpty()
        ) {
            button.isEnabled = false
            button.background = ResourcesCompat.getDrawable(
                button.context.resources,
                R.drawable.bg_button_disable_login,
                null
            )
        } else {
            button.isEnabled = true
            button.background =
                ResourcesCompat.getDrawable(
                    button.context.resources,
                    R.drawable.bg_button_enable_login,
                    null
                )
        }
    }

    @JvmStatic
    @BindingAdapter("app:VmBinding")
    fun searchQueue(editText: EditText, vm: ViewModel) {
        editText.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                if (editText.hasFocus()) {
                    editText.clearFocus()
                    val imm = editText.context.getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(editText.windowToken, 0)
                }
                when {
                    vm is HomeViewModel -> {
                        vm.actionSearch()
                    }
                }
            }
            false
        }
    }
}