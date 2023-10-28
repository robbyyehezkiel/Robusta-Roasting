package com.robbyyehezkiel.robustaroasting.ui.menu.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.databinding.ActivityAboutAppBinding

class AboutAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.toolbar_about_app)
        }
    }

}
