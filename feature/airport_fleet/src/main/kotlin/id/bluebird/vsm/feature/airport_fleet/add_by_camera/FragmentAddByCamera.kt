package id.bluebird.vsm.feature.airport_fleet.add_by_camera

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.otaliastudios.cameraview.Audio
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import id.bluebird.vsm.core.utils.DialogUtils
import id.bluebird.vsm.feature.airport_fleet.R
import id.bluebird.vsm.feature.airport_fleet.add_by_camera.AddByCameraAirportViewModel.Companion.EMPTY_STRING
import id.bluebird.vsm.feature.airport_fleet.databinding.AddByCameraAirportFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentAddByCamera : Fragment() {

    companion object {
        const val MY_PERMISSIONS_REQUEST_CAMERA: Int = 100
    }

    private lateinit var binding: AddByCameraAirportFragmentBinding
    private val addByCameraViewModel: AddByCameraAirportViewModel by viewModel()
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_by_camera_airport_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vm = addByCameraViewModel
            this.fleetType = 1L
        }


        val subLocationId = arguments?.getLong("subLocationId") ?: -1
        addByCameraViewModel.init(subLocationId)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(addByCameraViewModel) {
                    addByCameraState.collect {
                        when (it) {
                            is AddByCameraState.ProsesScan -> {
                                if(it.number.isEmpty()) {
                                    DialogUtils.showErrorDialog(requireContext(), EMPTY_STRING, getString(R.string.fleet_number_can_not_scan))
                                        .show()
                                } else {
                                    val msg =
                                        "${it.number} ${getString(R.string.car_success_add_in_stock)}"
                                    val message = SpannableStringBuilder()
                                        .append(msg)
                                    DialogUtils.showSnackbar(view, requireContext(), message, null, null)
                                    findNavController().popBackStack()
                                }
                            }
                            is AddByCameraState.OnError -> {
                                DialogUtils.showErrorDialog(
                                    requireContext(),
                                    getString(R.string.failed_to_add_stock),
                                    it.throwable.message.toString()
                                )
                            }
                            AddByCameraState.CancleScan -> {
                                findNavController().popBackStack()
                            }
                            AddByCameraState.RepeatTakePicture -> {
                                showCameraView()
                            }
                        }
                    }
                }
            }
        }
        setPermission()
        initCameraView()
        showCameraView()
        configureCamera()
    }

    override fun onPause() {
        if(binding.cameraView.isStarted) {
            binding.cameraView.stop()
        }
        super.onPause()
    }

    private fun initCameraView() {
        binding.cameraView.audio = Audio.OFF
        binding.cameraView.playSounds = false
        binding.cameraView.cropOutput = true
    }

    private fun setPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, MY_PERMISSIONS_REQUEST_CAMERA)
        }
    }


    private fun showCameraView() {
        binding.cameraView.start()
        setTakePicture()
    }

    private fun setTakePicture() {
        binding.cameraView.visibility = View.VISIBLE
        binding.imageView.visibility = View.GONE
        binding.btnRefresh.visibility = View.GONE
        binding.buttonTakePicture.visibility = View.VISIBLE
        binding.addDialogActionButton.visibility = View.GONE
        binding.textViewResult.text = ""
    }

    private fun configureCamera() {

        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                binding.cameraView.stop()
                CameraUtils.decodeBitmap(jpeg) { bitmap ->
                    binding.imageView.setImageBitmap(bitmap)
                    val image = InputImage.fromBitmap(bitmap, 0)
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            binding.cameraView.visibility = View.GONE
                            binding.imageView.visibility = View.VISIBLE
                            binding.btnRefresh.visibility = View.VISIBLE
                            binding.addDialogActionButton.visibility = View.VISIBLE
                            binding.buttonTakePicture.visibility = View.GONE
                            if(visionText.text.isEmpty() || visionText.text.length > 8) {
                                setIsNotGetNumber(getString(R.string.msg_not_get_picture))
                            } else {
                                binding.btnProsesAction.visibility = View.VISIBLE
                                binding.btnRepeatPictureAction.visibility = View.GONE
                                binding.textViewResult.text = visionText.text
                                addByCameraViewModel.param.value = visionText.text
                            }
                        }
                        .addOnFailureListener { e ->
                            setIsNotGetNumber(e.message.toString())
                        }
                    super.onPictureTaken(jpeg)
                }
            }
        })
        binding.buttonTakePicture.setOnClickListener {
            binding.cameraView.captureSnapshot()
        }
        binding.btnRefresh.setOnClickListener {
            showCameraView()
        }
    }

    fun setIsNotGetNumber(message: String) {
        binding.btnProsesAction.visibility = View.GONE
        binding.btnRepeatPictureAction.visibility = View.VISIBLE
        DialogUtils.showErrorDialog(requireContext(), getString(R.string.title_not_get_picture), message)
            .show()
    }
}