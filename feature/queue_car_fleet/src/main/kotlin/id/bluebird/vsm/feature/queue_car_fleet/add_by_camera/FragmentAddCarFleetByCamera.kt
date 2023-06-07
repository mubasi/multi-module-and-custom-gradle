package id.bluebird.vsm.feature.queue_car_fleet.add_by_camera

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView.ScaleType.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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
import id.bluebird.vsm.feature.queue_car_fleet.R
import id.bluebird.vsm.feature.queue_car_fleet.databinding.AddCarFleetByCameraFragmentBinding
import id.bluebird.vsm.feature.queue_car_fleet.databinding.AddCarFleetFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentAddCarFleetByCamera : Fragment() {

    companion object {
        const val RESULT = "resultAdd"
        const val RESULT_ADD = "resultNumber"
        const val MY_PERMISSIONS_REQUEST_CAMERA: Int = 100
    }

    private lateinit var binding: AddCarFleetByCameraFragmentBinding
    private val viewModel: AddCarFleetByCameraViewModel by viewModel()
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_car_fleet_by_camera_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.lifecycleOwner = viewLifecycleOwner
            this.vm = viewModel
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(viewModel) {
                    addCarFleetByCameraState.collect {
                        when (it) {
                           is AddCarFleetByCameraState.ProsesScan -> {
                               if(it.number.isEmpty()) {
                                   DialogUtils.showErrorDialog(requireContext(), "", "Nomor lambung tidak dapat di scan")
                                       .show()
                               } else {
                                   val bundle = Bundle()
                                   bundle.putString(RESULT_ADD, it.number)
                                   setFragmentResult(RESULT, bundle)
                                   findNavController().popBackStack()
                               }
                            }
                            AddCarFleetByCameraState.CancleScan -> {
                                findNavController().popBackStack()
                            }
                            AddCarFleetByCameraState.RepeatTakePicture -> {
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
            ActivityCompat.requestPermissions(requireActivity(), permissions, 100)
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
                            binding.imageView.scaleType = CENTER_CROP
                            if(visionText.text.isEmpty() || visionText.text.length > 8) {
                                setIsNotGetNumber(getString(R.string.msg_not_get_picture))
                            } else {
                                binding.btnProsesAction.visibility = View.VISIBLE
                                binding.btnRepeatPictureAction.visibility = View.GONE
                                binding.textViewResult.text = visionText.text
                                viewModel.param.value = visionText.text
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
        DialogUtils.showErrorDialog(requireContext(), getString(R.string.msg_not_get_picture_title), message)
            .show()
    }

}