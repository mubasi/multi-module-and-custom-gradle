package id.bluebird.mall.login

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:setCursorLastPosition")
    fun setCursorPosition(editText: EditText, text: String?) {
        if (text != null && text.isNotEmpty()) {
            editText.setSelection(text.length)
        }
    }

    @JvmStatic
    @BindingAdapter("app:passwordToggle")
    fun setPasswordToggle(imageView: ImageView, vm: LoginViewModel) {
        imageView.setImageResource(R.drawable.ic_mask_disable)
        imageView.setOnClickListener {
            vm.visibilityPassword.value = if (imageView.tag == "0") {
                imageView.tag = "1"
                imageView.setImageResource(R.drawable.ic_mask)
                true
            } else {
                imageView.tag = "0"
                imageView.setImageResource(R.drawable.ic_mask_disable)
                false
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:enable")
    fun setAppEnable(view: View, bool: Boolean?) {
        view.isClickable = bool == true
    }

    @JvmStatic
    @BindingAdapter("app:username", "app:password")
    fun setLoginButtonDisplay(button: Button, username: String?, password: String?) {
        val isDisplay = username == null || username.isEmpty()
                || password == null || password.isEmpty()
        setButtonDisplay(button, isDisplay)
    }

    private fun setButtonDisplay(button: Button, isDisplay: Boolean) {
        if (isDisplay) {
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
}