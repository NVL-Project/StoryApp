package com.nvl.storyapp.view.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nvl.storyapp.R
import com.nvl.storyapp.databinding.ActivityMainBinding
import com.nvl.storyapp.view.ViewModelFactory
import com.nvl.storyapp.view.add.AddActivity
import com.nvl.storyapp.view.maps.MapsActivity
import com.nvl.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false
    private val time : Long = 2000
    private val locationPermissionReqCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        getMyLocation()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        val storiesAdapter = MainAdapter()

        val preferences = this.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val bearer = "Bearer " + preferences.getString("token", "").toString()

        binding.rvUser.layoutManager = LinearLayoutManager(this)
        mainViewModel.getStories(bearer).observe(this){
            binding.rvUser.adapter = storiesAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storiesAdapter.retry()
                }
            )
            mainViewModel.getStories(bearer).observe(this) {
                storiesAdapter.submitData(lifecycle, it)
            }
        }
        binding.btnAddStory.setOnClickListener {
            val i = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(i)
        }
        
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.btn_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.btn_logout -> {
                Toast.makeText(
                    this,
                    getString(R.string.logout_success),
                    Toast.LENGTH_SHORT).show()
                mainViewModel.deleteUser()
                val i = Intent(this, WelcomeActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                finish()
                true
            }
            else -> true
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
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionReqCode)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            onBackPressedDispatcher.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.back_exit), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, time)
    }
    override fun onDestroy() {
        super.onDestroy()
        binding
    }

}