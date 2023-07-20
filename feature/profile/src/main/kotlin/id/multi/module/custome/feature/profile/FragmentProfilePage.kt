package id.multi.module.custome.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import id.multi.module.custome.feature.profile.databinding.ProfilePageFragmentBinding

class FragmentProfilePage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<ProfilePageFragmentBinding>(
            inflater,
            R.layout.profile_page_fragment,
            container,
            false
        )
        return binding.root
    }
}