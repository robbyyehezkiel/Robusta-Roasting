package com.robbyyehezkiel.robustaroasting.ui.roasting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import com.bumptech.glide.Glide
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Roast
import com.robbyyehezkiel.robustaroasting.databinding.ActivityDetailRoastingBinding

class DetailRoastingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRoastingBinding

    private lateinit var colorDescriptionTextView: View
    private lateinit var aromaDescriptionTextView: View
    private lateinit var flavourDescriptionTextView: View
    private var isExpandedColor = false
    private var isExpandedAroma = false
    private var isExpandedFlavour = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRoastingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roastData = getParcelableExtra(intent, "DATA", Roast::class.java)
        if (roastData != null) {
            displayRoastData(roastData)

        }

        initViews()
        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.toolbar_detail_roast)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun displayRoastData(roastData: Roast) {
        Glide.with(this)
            .load(roastData.photoResId)
            .centerCrop()
            .into(binding.roastPhoto)
        binding.roastTitle.text = roastData.title
        binding.roastTemperature.text = roastData.temperature
        binding.roastDescription.text = roastData.description
        binding.roastColor.text = roastData.color
        binding.roastAroma.text = roastData.aroma
        binding.roastFlavour.text = roastData.flavour
    }

    private fun initViews() {
        colorDescriptionTextView = binding.roastColor
        aromaDescriptionTextView = binding.roastAroma
        flavourDescriptionTextView = binding.roastFlavour
    }

    private fun setupClickListeners() {
        binding.roastColorTitle.setOnClickListener {
            toggleExpansionColor()
        }

        binding.roastAromaTitle.setOnClickListener {
            toggleExpansionAroma()
        }

        binding.roastFlavourTitle.setOnClickListener {
            toggleExpansionFlavour()
        }
    }

    private fun toggleExpansion(view: View, isExpanded: Boolean) {
        view.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    private fun updateTitleIcon(textView: TextView, isExpanded: Boolean) {
        val iconRes = if (isExpanded) {
            R.drawable.icon_expand_less
        } else {
            R.drawable.icon_expand_more
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, iconRes, 0)
    }

    private fun toggleExpansionColor() {
        isExpandedColor = !isExpandedColor
        toggleExpansion(colorDescriptionTextView, isExpandedColor)
        toggleExpansion(binding.colorSeparatorLine, isExpandedColor)
        updateTitleIcon(binding.roastColorTitle, isExpandedColor)
    }

    private fun toggleExpansionAroma() {
        isExpandedAroma = !isExpandedAroma
        toggleExpansion(aromaDescriptionTextView, isExpandedAroma)
        toggleExpansion(binding.aromaSeparatorLine, isExpandedAroma)
        updateTitleIcon(binding.roastAromaTitle, isExpandedAroma)
    }

    private fun toggleExpansionFlavour() {
        isExpandedFlavour = !isExpandedFlavour
        toggleExpansion(flavourDescriptionTextView, isExpandedFlavour)
        toggleExpansion(binding.flavourSeparatorLine, isExpandedFlavour)
        updateTitleIcon(binding.roastFlavourTitle, isExpandedFlavour)
    }

}