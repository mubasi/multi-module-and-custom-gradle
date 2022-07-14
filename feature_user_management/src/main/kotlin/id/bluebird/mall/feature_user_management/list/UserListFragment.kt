package id.bluebird.mall.feature_user_management.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.create.CreateUserFragment
import id.bluebird.mall.feature_user_management.utils.DialogUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {
    private val mUserSettingVM: UserManagementViewModel by viewModel()
    private lateinit var mBinding: id.bluebird.mall.feature_user_management.databinding.FragmentUserSettingBinding
    private var mContainer: ViewGroup? = null
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
        mContainer = container
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
                        gotoCreateUser(null)
                    }
                    is UserSettingSealed.EditUser -> {
                        gotoCreateUser(it.userSettingCache.id)
                    }
                    is UserSettingSealed.DeleteSuccess -> {
                        val message =
                            "${it.userSettingCache.userName} ${getString(R.string.delete_user_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                    is UserSettingSealed.ForceSuccess -> {
                        val message =
                            "${it.userSettingCache.userName} ${getString(R.string.force_logout_user_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

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
                    is UserSettingSealed.CreateUserSuccess -> {
                        val message =
                            "${it.name} ${getString(R.string.created_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }
                    is UserSettingSealed.EditUserSuccess -> {
                        val message =
                            "${it.name} ${getString(R.string.edited_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun gotoCreateUser(id: Long?) {
        val destination =
            UserListFragmentDirections.actionUserListFragmentToCreateUserFragment().apply {
                userId = id ?: 0
            }
        findNavController().navigate(destination)
        setFragmentResultListener(CreateUserFragment.REQUEST_KEY) { _, bundle ->
            bundle.let {
                mUserSettingVM.result(
                    it.getString(CreateUserFragment.NAME_PARAM, ""),
                    it.getBoolean(CreateUserFragment.ACTION_PARAM)
                )
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.rcvUserSetting.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvUserSetting.adapter = mAdapterUserSetting
    }
}