package id.bluebird.mall.feature_user_management.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.create.model.RoleCache
import id.bluebird.mall.feature_user_management.create.model.SubLocationCache
import id.bluebird.mall.feature_user_management.databinding.FragmentCreateUserBinding
import id.bluebird.mall.feature_user_management.databinding.ItemChipsLocationBinding
import id.bluebird.mall.feature_user_management.utils.DialogUtil
import id.bluebird.mall.feature_user_management.utils.NavControllerUserDomain
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateUserFragment : Fragment() {

    companion object {
        const val NAME_PARAM = "name"
        const val ACTION_PARAM = "action"
        const val REQUEST_KEY = "requestKey"
    }

    private val createUserViewModel: CreateUserViewModel by viewModel()
    private lateinit var mBinding: FragmentCreateUserBinding
    val args: CreateUserFragmentArgs by navArgs()
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
        createUserViewModel.initUser(args.userId)
        setListener()
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
                createChipsLocation(it)
            }
            roleLiveData.observe(viewLifecycleOwner) {
                createSpinnerAdapter(it)
            }
            subLocationSingleSelection.observe(viewLifecycleOwner) {
                if (!it) {
                    val chipGroup = mBinding.cpgLocationUser
                    for (i in 0 until chipGroup.childCount) {
                        chipGroup.getChildAt(i).isClickable = true
                    }
                }
            }
        }
        actionSealedObserver()
    }

    private fun actionSealedObserver() {
        with(createUserViewModel) {
            actionSealed.observe(viewLifecycleOwner) {
                when (it) {
                    CreateUserState.OnBack -> {
                        NavControllerUserDomain.popBack(findNavController())
                    }
                    is CreateUserState.GetInformationOnError -> TODO()
                    CreateUserState.GetInformationSuccess -> {
                        assignRole()
                        addSubLocation()
                    }
                    is CreateUserState.InvalidField -> {
                        DialogUtil.showErrorDialog(
                            requireContext(),
                            null,
                            ""
                        )
                    }
                    is CreateUserState.OnError -> {
                        DialogUtil.showErrorDialog(
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

    private fun setListener() {
        val chipGroup = mBinding.cpgLocationUser
        chipGroup.setOnCheckedChangeListener { _, _ ->
            val chip = chipGroup.getChildAt(chipGroup.checkedChipId) as Chip?
            if (chip != null) {
                for (i in 0 until chipGroup.childCount) {
                    chipGroup.getChildAt(i).isClickable = true
                }
                createUserViewModel.locationAssignment(chip.tag, true)
                chip.isClickable = false
            }
        }
    }

    private fun createSpinnerAdapter(roleCaches: List<RoleCache>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roleCaches)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spnUserSetting.adapter = adapter
    }

    private fun createChipsLocation(subLocationCaches: List<SubLocationCache>) {
        if (mBinding.cpgLocationUser.childCount > 0) {
            mBinding.cpgLocationUser.removeAllViews()
        }
        for (i in subLocationCaches.indices) {
            val element = subLocationCaches[i]
            val inflater = LayoutInflater.from(requireContext())
            val binding = DataBindingUtil.inflate<ItemChipsLocationBinding>(
                inflater,
                R.layout.item_chips_location,
                mBinding.cpgLocationUser,
                false
            )
            binding.lifecycleOwner = viewLifecycleOwner
            binding.subLocationCache = element
            binding.fleetTypeId = UserUtils.getFleetTypeId()
            binding.chipsItemLocation.isChecked = element.isSelected
            binding.chipsItemLocation.id = i
            binding.chipsItemLocation.setOnCheckedChangeListener { _, isChecked ->
                createUserViewModel.locationAssignment(element.id, isChecked)
            }
            mBinding.cpgLocationUser.addView(binding.root)
        }
        mBinding.cpgLocationUser.invalidate()
    }
}