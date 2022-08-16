package id.bluebird.mall.feature_user_management.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.bluebird.mall.core.utils.DialogUtils
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.create.model.RoleCache
import id.bluebird.mall.feature_user_management.databinding.FragmentCreateUserBinding
import id.bluebird.mall.feature_user_management.search_location.SearchLocationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateUserFragment : Fragment() {

    companion object {
        const val NAME_PARAM = "name"
        const val ACTION_PARAM = "action"
        const val REQUEST_KEY = "requestKey"
    }

    private val createUserViewModel: CreateUserViewModel by viewModel()
    private lateinit var mBinding: FragmentCreateUserBinding
    private val _args: CreateUserFragmentArgs by navArgs()
    private var isCreateUser = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_user, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.createUserVM = createUserViewModel
        createUserViewModel.initUser(_args.userId)
        observer()
        createUserViewModel.getUser()
    }

    private fun observer() {
        with(createUserViewModel) {
            userRolePosition.observe(viewLifecycleOwner) {
                if (mBinding.spnUserSetting.childCount > 0) {
                    mBinding.spnUserSetting.setSelection(it)
                }
            }
            userName.observe(viewLifecycleOwner) {
                usernameTextHandler(it)
            }
            subLocationLiveData.observe(viewLifecycleOwner) {

            }
            roleLiveData.observe(viewLifecycleOwner) {
                createSpinnerAdapter(it)
            }
        }
        actionSealedObserver()
    }

    private fun actionSealedObserver() {
        with(createUserViewModel) {
            actionSealed.observe(viewLifecycleOwner) {
                when (it) {
                    CreateUserState.OnBack -> {
                        findNavController().popBackStack(
                            destinationId = R.id.createUserFragment,
                            inclusive = true,
                            saveState = false
                        )
                    }
                    is CreateUserState.GetInformationOnError -> TODO()
                    CreateUserState.GetInformationSuccess -> {
                        assignRole()
                        addSubLocation()
                    }
                    is CreateUserState.InvalidField -> {
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            null,
                            ""
                        )
                    }
                    is CreateUserState.OnError -> {
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            null,
                            it.err.message ?: ""
                        )
                    }
                    is CreateUserState.GetUserStateSuccess -> {
                        getInformation()
                    }
                    is CreateUserState.OnSuccess -> {
                        val bundle = Bundle()
                        bundle.putString(NAME_PARAM, it.name)
                        bundle.putBoolean(ACTION_PARAM, it.isCreateUser)
                        setFragmentResult(REQUEST_KEY, bundle)
                        findNavController().popBackStack()
                    }
                    is CreateUserState.RequestSearchLocation -> {
                        navigateToSearchLocation()
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun usernameTextHandler(it: String) {
        if (it.isNotEmpty() && isCreateUser) {
            mBinding.edtUsernameCreateUser.setSelection(it.length)
        }
        if (it.contains(" ")) {
            createUserViewModel.userName.value = it.replace(" ", "").trim()
        }
    }

    private fun createSpinnerAdapter(roleCaches: List<RoleCache>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roleCaches)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spnUserSetting.adapter = adapter
    }

    private fun navigateToSearchLocation() {
        val destination = CreateUserFragmentDirections.actionCreateUserFragmentToSearchLocationFragment()
        findNavController().navigate(destination)
        setFragmentResultListener(SearchLocationFragment.REQUEST_KEY) { _, bundle ->
            createUserViewModel.setSelectedLocation(bundle.getParcelable(SearchLocationFragment.RESULT_KEY))
        }
    }
}