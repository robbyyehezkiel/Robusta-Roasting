package com.robbyyehezkiel.robustaroasting.ui.menu.coffee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.databinding.ActivityDetailCoffeeBinding

class DetailCoffeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCoffeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCoffeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.toolbar_detail_coffee)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}
