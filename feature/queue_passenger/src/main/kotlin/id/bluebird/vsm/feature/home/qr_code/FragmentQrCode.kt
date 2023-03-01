package id.bluebird.vsm.feature.home.qr_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import id.bluebird.vsm.core.extensions.StringExtensions.convertBase64
import id.bluebird.vsm.feature.home.R
import id.bluebird.vsm.feature.home.databinding.FragmentQrCodeBinding
import id.bluebird.vsm.feature.home.qr_code.QrCodeViewModel.Companion.EMPTY_STRING
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentQrCode : Fragment() {

    private val _vm: QrCodeViewModel by viewModel()
    private lateinit var binding: FragmentQrCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_qr_code,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.isLoading = false
        }
        setupTabLayout()
        setArgument()
        setupFirst()
        reloadQrcode()
        observer()
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                _vm.qrCodeState.collect {
                    when (it) {
                        QrCodeState.Progress -> {
                            binding.isLoading = true
                        }
                        is QrCodeState.SuccessLoad -> {
                            binding.isLoading = false
                            showQrCode(it.image)
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
            }
        }
        _vm.titleLocation.observe(viewLifecycleOwner) {
            setFragmentLabel(it)
        }
    }

    private fun setArgument() {
        if (arguments != null) {
            val titleLocation = arguments?.getString("titleLocation") ?: EMPTY_STRING
            val subLocationId = arguments?.getLong("subLocationId") ?: 0
            val locationId = arguments?.getLong("locationId") ?: 0
            _vm.init(locationId, subLocationId, titleLocation)
            setResultArgument()
        }
    }

    private fun setResultArgument() {
        binding.apply {
            idLocationText.text = _vm._locationId.toString()
            idSubLocationText.text = _vm._subLocationId.toString()
        }
    }

    private fun setFragmentLabel(label: String) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = label
    }

    private fun changeQrCode(position: Int) {
        binding.isLoading = true
        _vm.changeQrCode(position)
    }

    private fun setupFirst() {
        val selectedPosition = binding.tabLayout.selectedTabPosition
        changeQrCode(selectedPosition)
    }

    private fun showQrCode(result: String) {
        if (result.isEmpty()) {
            binding.imageQrPage.setImageDrawable(requireActivity().getDrawable(R.drawable.bg_queue_not_found))
            binding.isError = true
        } else {
            binding.imageQrPage.setImageBitmap(result.convertBase64())
            binding.isError = false
        }
    }

    private fun reloadQrcode() {
        binding.reloadQrCode.setOnClickListener {
            setupFirst()
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { changeQrCode(it) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Write code to handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Write code to handle tab reselect
            }
        })
    }

}