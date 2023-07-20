package id.multi.module.custome.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import id.multi.module.custome.home.R
import id.multi.module.custome.home.databinding.HomePageFragmentBinding
import id.multi.module.custome.navigation.NavigationNav
import id.multi.module.custome.navigation.NavigationSealed

class FragmentHomePage : Fragment() {

    private lateinit var mBinding : HomePageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_page_fragment,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.layoutButtonPage.setOnClickListener {
            NavigationNav.navigate(
                NavigationSealed.ProfilePage (
                    destination = null,
                    frag = this@FragmentHomePage,
                )
            )
        }

    }




}