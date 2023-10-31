package com.robbyyehezkiel.robustaroasting.ui.roast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.databinding.ActivityRoastingBinding
import com.robbyyehezkiel.robustaroasting.ui.menu.detection.DetectionResultActivity

class RoastingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoastingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoastingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            val intentToDetection = Intent(this, DetectionResultActivity::class.java)
            startActivity(intentToDetection)
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.menu_roasting)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}