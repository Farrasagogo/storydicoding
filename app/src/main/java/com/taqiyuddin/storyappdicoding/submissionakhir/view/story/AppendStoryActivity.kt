package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.taqiyuddin.storyappdicoding.submissionakhir.R
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityAppendStoryBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.reduceFileImage
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class AppendStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppendStoryBinding
    private lateinit var viewModel: AppendStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedImageUri: Uri? = null
    private var currentLocation: Location? = null

    companion object {
        private const val STATE_IMAGE_URI = "state_image_uri"
        private const val STATE_DESCRIPTION = "state_description"
        private const val STATE_LOCATION_ENABLED = "state_location_enabled"
        private const val STATE_LATITUDE = "state_latitude"
        private const val STATE_LONGITUDE = "state_longitude"
    }

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.CAMERA] == true -> {
                openCamera()
            }
            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionRequest.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri?.let { uri ->
                binding.previewImageView.setImageURI(uri)
            }
        } else {
            selectedImageUri = null
            Toast.makeText(this, getString(R.string.camera_capture_canceled), Toast.LENGTH_SHORT).show()
        }
    }


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.previewImageView.setImageURI(selectedImageUri)
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getLastLocation()
            }
            else -> {
                Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_SHORT).show()
                binding.cbLocation.isChecked = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppendStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, CreateStoryViewModelFactory(this))[AppendStoryViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupViews()
        observeViewModel()

        savedInstanceState?.let { restoreState(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelable(STATE_IMAGE_URI, selectedImageUri)
            putString(STATE_DESCRIPTION, binding.edAddDescription.text.toString())
            putBoolean(STATE_LOCATION_ENABLED, binding.cbLocation.isChecked)
            currentLocation?.let { location ->
                putDouble(STATE_LATITUDE, location.latitude)
                putDouble(STATE_LONGITUDE, location.longitude)
            }
        }
    }

    private fun restoreState(savedInstanceState: Bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            selectedImageUri = savedInstanceState.getParcelable(STATE_IMAGE_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            selectedImageUri = savedInstanceState.getParcelable(STATE_IMAGE_URI)
        }
        selectedImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }

        binding.edAddDescription.setText(savedInstanceState.getString(STATE_DESCRIPTION, ""))

        val locationEnabled = savedInstanceState.getBoolean(STATE_LOCATION_ENABLED, false)
        if (locationEnabled) {
            binding.cbLocation.isChecked = true
            val latitude = savedInstanceState.getDouble(STATE_LATITUDE, 0.0)
            val longitude = savedInstanceState.getDouble(STATE_LONGITUDE, 0.0)
            currentLocation = Location("").apply {
                this.latitude = latitude
                this.longitude = longitude
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.cameraButton.setOnClickListener { checkCameraPermission() }
        binding.galleryButton.setOnClickListener { openGallery() }
        binding.uploadButton.setOnClickListener { uploadStory() }

        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkLocationPermissions()
            else currentLocation = null
        }
    }

    private fun openCamera() {
        selectedImageUri = getImageUri(this)
        cameraLauncher.launch(selectedImageUri!!)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.resetError()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        try {
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } catch (e: Exception) {
            null
        }

        return uri ?: throw IllegalStateException("Could not create image URI")
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
                if (location != null) {
                    Toast.makeText(this, getString(R.string.get_location), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                    binding.cbLocation.isChecked = false
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_SHORT).show()
            binding.cbLocation.isChecked = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadStory() {
        if (selectedImageUri == null) {
            Toast.makeText(this, getString(R.string.please_choose_a_picture_first), Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edAddDescription.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_add_description), Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressIndicator.visibility = View.VISIBLE

        val imageFile = uriToFile(selectedImageUri!!, this).reduceFileImage()
        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("photo", imageFile.name, imageBody)

        val lat = if (binding.cbLocation.isChecked && currentLocation != null) {
            currentLocation?.latitude.toString()
        } else "0"
        val long = if (binding.cbLocation.isChecked && currentLocation != null) {
            currentLocation?.longitude.toString()
        } else "0"

        viewModel.uploadStory(
            description = descriptionBody,
            photo = imagePart,
            lat = lat.toRequestBody("text/plain".toMediaTypeOrNull()),
            long = long.toRequestBody("text/plain".toMediaTypeOrNull())
        ).also {
            viewModel.createStoryResponse.observe(this) { response ->
                if (response.isError == false) {
                    Toast.makeText(this, getString(R.string.story_uploaded_successfully), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StoryActivity::class.java).apply {
                        putExtra("scrollToTop", true)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}