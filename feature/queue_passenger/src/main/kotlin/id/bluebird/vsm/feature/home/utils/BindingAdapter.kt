package id.bluebird.vsm.feature.home.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.model.QueueCache

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:setCursorLastPosition")
    fun setCursorPosition(editText: EditText, text: String?) {
        if (text != null && text.isNotEmpty()) {
            editText.setSelection(text.length)
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
//                when {
//                    vm is HomeViewModel -> {
//                        vm.actionSearch()
//                    }
//                }
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

    @JvmStatic
    @BindingAdapter("app:displayQueueTitle")
    fun setMessageForQueueTittle(textView: TextView, queueCache: QueueCache?) {
        queueCache?.let {
            textView.text = when {
                queueCache.isCurrentQueue -> textView.context.getString(R.string.current_queue)
                queueCache.isDelay -> textView.context.getString(R.string.delay)
                else -> textView.context.getString(R.string.waiting)
            }
        }
    }
}