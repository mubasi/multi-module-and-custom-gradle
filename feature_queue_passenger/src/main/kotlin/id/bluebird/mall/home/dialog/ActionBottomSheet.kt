package id.bluebird.mall.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.bluebird.mall.core.HomeState
import id.bluebird.mall.home.HomeViewModel
import id.bluebird.mall.home.R
import id.bluebird.mall.home.databinding.BottomActionDialogBinding
import id.bluebird.mall.home.model.QueueCache
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActionBottomSheet(
    private val action: Action,
    private val queue: QueueCache?
) : BottomSheetDialogFragment() {
    private val mHomeViewModel: HomeViewModel by sharedViewModel()

    constructor(action: Action) : this(action, null)

    companion object {
        const val TAG = "actionBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCancelable(false)
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listenHomeState()
        return requireContext().let {
            val bottomDialog = BottomSheetDialog(it, R.style.SheetDialog)
            val view = LayoutInflater.from(it).inflate(R.layout.bottom_action_dialog, null)
            bottomDialog.window?.also { window ->
                window.setBackgroundDrawableResource(android.R.color.transparent)
            }
            val binding = DataBindingUtil.bind<BottomActionDialogBinding>(view)
            binding?.let { bind ->
                bottomDialog.setContentView(bind.root)
                bind.title = getTittle()
                bind.yesAction = getStringActionYes()
                bind.dialog = bottomDialog
                bind.item = queue
                bind.vm = mHomeViewModel
                bind.action = action
            }
            bottomDialog.show()
            bottomDialog
        }
    }

    private fun getTittle(): String {
        if (action == Action.LOGOUT) {
            return getString(R.string.exit_message)
        }
        val prefix = when (action) {
            Action.SKIP -> getString(R.string.skip_queue_number)
            else -> getString(R.string.restore_queue_number)
        }
        return "$prefix ${queue?.getQueue()} ?"
    }

    private fun getStringActionYes() = when (action) {
        Action.SKIP -> getString(R.string.skip)
        Action.RESTORE -> getString(R.string.restore)
        Action.LOGOUT -> getString(R.string.exit)
    }

    private fun listenHomeState() {
        mHomeViewModel.homeState.observe(this) {
            when (it) {
                is HomeState.SuccessSkiped,
                is HomeState.SuccessRestored,
                is HomeState.LogoutSuccess -> {
                    mHomeViewModel.homeDialogStateIdle()
                    dismiss()
                }
            }
        }
    }

}

enum class Action {
    SKIP, RESTORE, LOGOUT
}
