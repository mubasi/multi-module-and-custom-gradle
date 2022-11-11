package id.bluebird.vsm.feature.user_management.list

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.bluebird.vsm.feature.user_management.R
import id.bluebird.vsm.feature.user_management.create.CreateUserFragment
import id.bluebird.vsm.feature.user_management.utils.DialogUtil
import id.bluebird.vsm.feature.user_management.utils.ModifyUserAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {
    companion object {
        private const val EMPTY_STRING = ""
    }
    private val mUserSettingVM: UserManagementViewModel by viewModel()
    private lateinit var mBinding: id.bluebird.vsm.feature.user_management.databinding.FragmentUserSettingBinding
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

    override fun onDestroyView() {
        super.onDestroyView()
        mUserSettingVM.setIdle()
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
                    is UserSettingSealed.CreateUser -> {
                        gotoCreateUser(null, EMPTY_STRING)
                    }
                    is UserSettingSealed.EditUser -> {
                        gotoCreateUser(it.userSettingCache.id, it.userSettingCache.uuid)
                    }
                    is UserSettingSealed.DeleteSuccess -> {
                        val message =
                            "${it.name} ${getString(R.string.delete_user_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                    is UserSettingSealed.ForceSuccess -> {
                        val message =
                            "${it.name} ${getString(R.string.force_logout_user_success)}"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }
                    is UserSettingSealed.GetUserOnError -> {
                        val msg = getString(R.string.get_users_failed)
                        DialogUtil.showSnackbar(view, Html.fromHtml("$msg",1), R.color.error_color)
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

    private fun gotoCreateUser(id: Long?, uuid: String) {
        val destination =
            UserListFragmentDirections.actionUserListFragmentToCreateUserFragment().apply {
                userId = id ?: 0
                this.uuid = uuid
            }
        findNavController().navigate(destination)
        setFragmentResultListener(CreateUserFragment.REQUEST_KEY) { _, bundle ->
            bundle.let {
                mUserSettingVM.result(
                    it.getString(CreateUserFragment.NAME_PARAM, EMPTY_STRING),
                    it.getParcelable(CreateUserFragment.ACTION_PARAM) ?: ModifyUserAction.Nothing
                )
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.rcvUserSetting.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvUserSetting.adapter = mAdapterUserSetting
    }
}