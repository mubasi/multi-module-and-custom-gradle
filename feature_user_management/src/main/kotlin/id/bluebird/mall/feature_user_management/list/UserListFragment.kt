package id.bluebird.mall.feature_user_management.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.utils.DialogUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {
    private val mUserSettingVM: UserManagementViewModel by viewModel()
    private lateinit var mBinding: id.bluebird.mall.feature_user_management.databinding.FragmentUserSettingBinding
    private val mAdapterUserSetting: AdapterUserSetting by lazy {
        AdapterUserSetting(userSettingViewModel = mUserSettingVM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_setting, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.userSettingViewModel = mUserSettingVM
        initRecyclerView()
        mUserSettingVM.init()
        mUserSettingVM.searchUser()

        with(mUserSettingVM) {
            userSettingSealed.observe(viewLifecycleOwner) {
                when (it) {
                    is UserSettingSealed.Delete -> DialogUtil.actionDialogUser(
                        requireContext(),
                        context?.getString(R.string.delete_user),
                        context?.getString(R.string.delete_user_message)!!,
                        it.userSettingCache.userName
                    ) {
                        delete(it.userSettingCache)
                    }
                    is UserSettingSealed.ForceLogout -> DialogUtil.actionDialogUser(
                        requireContext(),
                        context?.getString(R.string.non_active_user),
                        context?.getString(R.string.non_active_user_message)!!,
                        it.userSettingCache.userName
                    ) {
                        forceLogout(it.userSettingCache)
                    }
                    is UserSettingSealed.CreateUser -> {
                    }
                    is UserSettingSealed.EditUser -> {
                    }
                    is UserSettingSealed.DeleteSuccess -> {
                        val message =
                            "${it.userSettingCache.userName} ${getString(R.string.delete_user_success)}"
                        DialogUtil.topSnackBar(mBinding.clMainCreateUser, message, null, null)
                    }
                    is UserSettingSealed.ForceSuccess -> {
                        val message =
                            "${it.userSettingCache.userName} ${getString(R.string.force_logout_user_success)}"
                        DialogUtil.topSnackBar(mBinding.clMainCreateUser, message, null, null)

                    }
                    is UserSettingSealed.GetUserOnError -> {
                        DialogUtil.showErrorDialog(
                            requireContext(),
                            getString(R.string.get_users_failed),
                            "error"
                        )
                    }
                    is UserSettingSealed.GetUsers -> {
                        mAdapterUserSetting.addNewSetData(it.list)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.rcvUserSetting.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvUserSetting.adapter = mAdapterUserSetting
    }
}