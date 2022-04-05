package id.bluebird.mall.officer.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.ui.home.HomeViewModel
import id.bluebird.mall.officer.ui.home.model.QueueCache
import id.bluebird.mall.officer.ui.login.LoginViewModel

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
    @BindingAdapter("app:show")
    fun setVisibility(view: View, bool: Boolean?) {
        view.visibility = if (bool == false) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:showw")
    fun setVisibility(view: View, item: QueueCache?) {
        view.visibility = if (!item!!.isCurrentQueue) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:username", "app:password")
    fun setLoginButtonDisplay(button: Button, username: String?, password: String?) {
        val isDisplay = username == null || username.isEmpty()
                || password == null || password.isEmpty()
        setButtonDisplay(button, isDisplay)
    }

    @JvmStatic
    @BindingAdapter("app:displayButton")
    fun setDisplayButton(button: Button, isDisplay: Boolean) {
        setButtonDisplay(button, isDisplay.not())
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

    @JvmStatic
    @BindingAdapter("app:successQueueButton")
    fun successQueueButton(imageView: ImageView, isCurrentQueue: Boolean) {
        if (isCurrentQueue) {
            imageView.setBackgroundResource(R.drawable.bg_success_enable)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_fill,
                    null
                )
            )
        } else {
            imageView.setBackgroundResource(R.drawable.bg_queue_disable)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_fill_disable,
                    null
                )
            )
        }
    }

    @JvmStatic
    @BindingAdapter("app:delayQueueButton")
    fun delayQueueButton(imageView: ImageView, isCurrentQueue: Boolean) {
        if (isCurrentQueue) {
            imageView.setBackgroundResource(R.drawable.bg_delay_enable)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_cross,
                    null
                )
            )
        } else {
            imageView.setBackgroundResource(R.drawable.bg_queue_disable)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_cross_disable,
                    null
                )
            )
        }
    }

    @JvmStatic
    @BindingAdapter("app:callQueueButton")
    fun callQueueButton(imageView: ImageView, bool: Boolean) {
        if (bool) {
            imageView.setBackgroundResource(android.R.color.transparent)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_announcement_enable,
                    null
                )
            )
        } else {
            imageView.setBackgroundResource(R.drawable.bg_queue_disable)
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    imageView.context.resources,
                    R.drawable.ic_announcement_disable,
                    null
                )
            )
        }
    }

    @JvmStatic
    @BindingAdapter("app:cancelDialogRitase")
    fun cancelDialogRitase(view: View, dialog: Dialog) {
        view.setOnClickListener {
            dialog.cancel()
        }
    }
}