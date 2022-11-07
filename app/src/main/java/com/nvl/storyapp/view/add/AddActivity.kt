package com.nvl.storyapp.view.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nvl.storyapp.R
import com.nvl.storyapp.camera.createCustomTempFile
import com.nvl.storyapp.camera.reduceFileImage
import com.nvl.storyapp.camera.uriToFile
import com.nvl.storyapp.databinding.ActivityAddBinding
import com.nvl.storyapp.view.ViewModelFactory
import com.nvl.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddActivity : AppCompatActivity(), LocationListener {
    private lateinit var binding: ActivityAddBinding
    private lateinit var mainViewModel: AddViewModel
    private var bearer: String = ""
    private lateinit var locationManager: LocationManager
    private var gpsStatus = false
    private val locationPermissionReqCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val preferences = this.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        bearer = "Bearer " + preferences.getString("token", "").toString()

        binding.btnLocStory.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        binding.btnLocStory.setOnClickListener {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onGPS()
            } else {
                getMyLocation()
            }
        }
        setupViewModel()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else
            return
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[AddViewModel::class.java]

        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }
        binding.btnGalery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            if (getFile != null) {
                val desc = binding.inputDesc.text.toString()
                val lati = binding.latValue.text.toString()
                val loni = binding.lonValue.text.toString()
                if (desc.isNotEmpty()) {
                    val file = reduceFileImage(getFile as File)
                    val description = desc.toRequestBody("text/plain".toMediaType())
                    val lat = lati.toRequestBody("text/plain".toMediaType())
                    val lon = loni.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    if (lati !="0.0" && loni !="0.0") {
                        mainViewModel.uploadStory(bearer, imageMultipart, description, lat, lon)
                            .observe(this) {
                                if (it.error) {
                                    Toast.makeText(
                                        this@AddActivity,
                                        it.message,
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        this@AddActivity,
                                        getString(R.string.story_upload),
                                        Toast.LENGTH_SHORT).show()
                                    val i = Intent(this, MainActivity::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(i)
                                    finish()
                                }
                            }
                    } else{
                        Toast.makeText(
                            this@AddActivity,
                            getString(R.string.add_loc),
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@AddActivity,
                        getString(R.string.add_description),
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this@AddActivity,
                    getString(R.string.add_image),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                "com.nvl.storyapp.view.add",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.not_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionReqCode)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        binding.latValue.text = "${location.latitude}"
        binding.lonValue.text = "${location.longitude}"
    }

    private fun onGPS() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.enableGPS)).setCancelable(false)
            .setPositiveButton(getString(
                R.string.yes)
            ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            Glide.with(this)
                .load(result)
                .into(binding.tvImage)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddActivity)
            getFile = myFile
            Glide.with(this)
                .load(myFile)
                .into(binding.tvImage)
        }
    }

    private var getFile: File? = null

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}