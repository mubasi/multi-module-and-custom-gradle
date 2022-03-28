package id.bluebird.mall.officer.ui.home.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import id.bluebird.mall.officer.R
import id.bluebird.mall.officer.common.HomeState
import id.bluebird.mall.officer.databinding.DialogRitaseBinding
import id.bluebird.mall.officer.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RitaseDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "ritaseSuccess"
    }

    private val mHomeViewModel: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.let {
            it.requestWindowFeature(STYLE_NO_TITLE)
            it.setCancelable(false)
        }
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listenHomeState()
        return context.let {
            val binding = DataBindingUtil.inflate<DialogRitaseBinding>(
                LayoutInflater.from(context),
                R.layout.dialog_ritase, null, false
            )
            binding.lifecycleOwner = this
            binding.vm = mHomeViewModel
            val builder = AlertDialog.Builder(it)
                .setView(binding.root)
            val dialog = builder.create()
            binding.dialog = dialog
            dialog
        }
    }

    private fun listenHomeState() {
        mHomeViewModel.homeState.observe(this) {
            when (it) {
                is HomeState.SuccessRitase -> {
                    dismiss()
                }
            }
        }
    }
}