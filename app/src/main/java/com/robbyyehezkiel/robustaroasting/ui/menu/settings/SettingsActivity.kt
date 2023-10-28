package com.robbyyehezkiel.robustaroasting.ui.menu.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.databinding.ActivitySettingsBinding
import com.robbyyehezkiel.robustaroasting.utils.openSettingPermission

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* toolbar */
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.toolbar_settings)
        binding.edSettingsApplicationLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.edSettingsApplicationPermission.setOnClickListener {
            openSettingPermission(this@SettingsActivity)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}