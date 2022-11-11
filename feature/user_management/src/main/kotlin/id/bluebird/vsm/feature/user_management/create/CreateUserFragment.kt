package id.bluebird.vsm.feature.user_management.create

import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.bluebird.vsm.feature.user_management.R
import id.bluebird.vsm.feature.user_management.create.model.RoleCache
import id.bluebird.vsm.feature.user_management.create.model.SubLocationCache
import id.bluebird.vsm.feature.user_management.databinding.FragmentCreateUserBinding
import id.bluebird.vsm.feature.user_management.search_location.SearchLocationFragment
import id.bluebird.vsm.feature.user_management.utils.DialogUtil
import id.bluebird.vsm.feature.user_management.utils.ModifyUserAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateUserFragment : Fragment() {

    companion object {
        const val NAME_PARAM = "name"
        const val ACTION_PARAM = "action"
        const val REQUEST_KEY = "requestKey"
        const val DELETE_REQ = "deleteRequest"
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
        createUserViewModel.initUser(_args.userId, _args.uuid)
        if (_args.userId > 0) {
            setHasOptionsMenu(true)
        }
        observer()
    }

    private fun observer() {
        with(createUserViewModel) {
            userRolePosition.observe(viewLifecycleOwner) {
                if (mBinding.spnUserSetting.childCount > 0) {
                    mBinding.spnUserSetting.setSelection(it)
                }
            }
            subLocationPosition.observe(viewLifecycleOwner) {
                if (mBinding.spinnerSubLocation.childCount > 0) {
                    mBinding.spinnerSubLocation.setSelection(it)
                }
            }
            userName.observe(viewLifecycleOwner) {
                usernameTextHandler(it)
            }
            subLocationLiveData.observe(viewLifecycleOwner) {
                createSpinnerSubLocation(it)
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
                    CreateUserState.Initialize -> {
                        createUserViewModel.getUser()
                    }
                    CreateUserState.OnBack -> {
                        findNavController().popBackStack(
                            destinationId = R.id.createUserFragment,
                            inclusive = true,
                            saveState = false
                        )
                    }
                    is CreateUserState.GetInformationOnError -> TODO()
                    is CreateUserState.GetInformationSuccess -> {
                        assignRole()
                        assignLocation()
                    }
                    is CreateUserState.InvalidField -> {
                        var currentView = requireActivity().window.decorView.rootView
                        DialogUtil.showSnackbar(currentView, Html.fromHtml("",1), R.color.error_color)
                    }
                    is CreateUserState.OnError -> {
                        var currentView = requireActivity().window.decorView.rootView
                        DialogUtil.showSnackbar(currentView, Html.fromHtml("",1), R.color.error_color)
                    }
                    is CreateUserState.GetUserStateSuccess -> {
                        getInformation()
                    }
                    is CreateUserState.OnSuccess -> {
                        val bundle = Bundle()
                        bundle.putString(NAME_PARAM, it.name)
                        bundle.putParcelable(
                            ACTION_PARAM,
                            if (it.isCreateUser) ModifyUserAction.Create else ModifyUserAction.Edit
                        )
                        setFragmentResult(REQUEST_KEY, bundle)
                        findNavController().popBackStack()
                    }
                    is CreateUserState.RequestSearchLocation -> {
                        navigateToSearchLocation()
                    }
                    is CreateUserState.LocationSelected -> {
                        setupSubLocation()
                    }
                    is CreateUserState.AssignSubLocationFromData -> {
                        setupSubLocation()
                    }
                    is CreateUserState.DeleteUser -> {
                        DialogUtil.actionDialogUser(
                            requireContext(),
                            getString(R.string.delete_user),
                            getString(R.string.delete_user_message),
                            it.name
                        ) {
                            delete()
                        }
                    }
                    is CreateUserState.OnSuccessDeleteUser -> {
                        val bundle = Bundle()
                        bundle.putString(NAME_PARAM, it.name)
                        bundle.putParcelable(ACTION_PARAM, ModifyUserAction.Delete)
                        setFragmentResult(REQUEST_KEY, bundle)
                        findNavController().popBackStack()
                    }
                    is CreateUserState.ForceLogout -> {
                        DialogUtil.actionDialogUser(
                            requireContext(),
                            getString(R.string.non_active_user),
                            getString(R.string.non_active_user_message),
                            it.name
                        ) {
                            forceLogout()
                        }
                    }
                    is CreateUserState.OnSuccessForceLogout -> {
                        val bundle = Bundle()
                        bundle.putString(NAME_PARAM, it.name)
                        bundle.putParcelable(ACTION_PARAM, ModifyUserAction.ForceLogout)
                        setFragmentResult(REQUEST_KEY, bundle)
                        findNavController().popBackStack()
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete_user -> {
                createUserViewModel.requestDelete()
                true
            }
            R.id.force_logout -> {
                createUserViewModel.requestForceLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manage_user_action, menu)
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

    private fun createSpinnerSubLocation(subLocationCache: List<SubLocationCache>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subLocationCache)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerSubLocation.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        createUserViewModel.toIdle()
    }

    private fun navigateToSearchLocation() {
        val destination = CreateUserFragmentDirections.actionCreateUserFragmentToSearchLocationFragment()
        findNavController().navigate(destination)
        setFragmentResultListener(SearchLocationFragment.REQUEST_KEY) { _, bundle ->
            createUserViewModel.setSelectedLocation(bundle.getParcelable(SearchLocationFragment.RESULT_KEY))
        }
    }
}