package com.robbyyehezkiel.robustaroasting.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Menu
import com.robbyyehezkiel.robustaroasting.databinding.ActivityMainBinding
import com.robbyyehezkiel.robustaroasting.ui.menu.app.AboutAppActivity
import com.robbyyehezkiel.robustaroasting.ui.menu.coffee.DetailCoffeeActivity
import com.robbyyehezkiel.robustaroasting.ui.menu.detection.DetectionResultActivity
import com.robbyyehezkiel.robustaroasting.ui.menu.settings.SettingsActivity
import com.robbyyehezkiel.robustaroasting.ui.roast.RoastingActivity
import com.robbyyehezkiel.robustaroasting.ui.roasting.DetailRoastingActivity
import com.robbyyehezkiel.robustaroasting.utils.getListRoast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val menuItemClickActions = mapOf(
        "first menu" to ::startAboutAppActivity,
        "second menu" to ::startDetailCoffeeActivity,
        "third menu" to ::startQuizActivity,
        "fourth menu" to ::startSettingsActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoastRecyclerView()
        setupMenuRecyclerView()
        binding.buttonScan.setOnClickListener {
            startDetectionResultActivity()
        }
    }

    private fun setupRoastRecyclerView() {
        val roastListAdapter = RoastListAdapter { roast, _ ->
            val intentToDetail = Intent(this, DetailRoastingActivity::class.java).apply {
                putExtra("DATA", roast)
            }
            startActivity(intentToDetail)
        }

        binding.roastRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = roastListAdapter
        }

        roastListAdapter.submitList(getListRoast(this@MainActivity))
    }

    private fun setupMenuRecyclerView() {
        binding.menuRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = MainMenuAdapter(getListMenus()) { data ->
                menuItemClickActions[data.menuId]?.invoke()
            }
        }
    }

    private fun startDetectionResultActivity() {
        startActivity(Intent(this, DetectionResultActivity::class.java))
    }

    private fun startAboutAppActivity() {
        startActivity(Intent(this, AboutAppActivity::class.java))
    }

    private fun startDetailCoffeeActivity() {
        startActivity(Intent(this, DetailCoffeeActivity::class.java))
    }

    private fun startQuizActivity() {
        startActivity(Intent(this, RoastingActivity::class.java))
    }

    private fun startSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun getListMenus(): List<Menu> {
        val dataId = resources.getStringArray(R.array.data_id)
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        return dataName.indices.map { i ->
            Menu(dataId[i], dataName[i], dataPhoto.getResourceId(i, -1))
        }.also { dataPhoto.recycle() }
    }

}