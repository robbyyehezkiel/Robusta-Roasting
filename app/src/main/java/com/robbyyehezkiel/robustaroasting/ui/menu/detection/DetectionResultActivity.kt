package com.robbyyehezkiel.robustaroasting.ui.menu.detection

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Roast
import com.robbyyehezkiel.robustaroasting.databinding.ActivityDetectionResultBinding
import com.robbyyehezkiel.robustaroasting.databinding.DialogResultBinding
import com.robbyyehezkiel.robustaroasting.ml.MobileNetModelSeleksi
import com.robbyyehezkiel.robustaroasting.ui.roasting.DetailRoastingActivity
import com.robbyyehezkiel.robustaroasting.utils.createCustomTempFile
import com.robbyyehezkiel.robustaroasting.utils.getListRoast
import com.robbyyehezkiel.robustaroasting.utils.showDialogInfo
import com.robbyyehezkiel.robustaroasting.utils.snackBarAction
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

class DetectionResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectionResultBinding
    private lateinit var selectBtn: Button
    private lateinit var takePictureBtn: Button
    private lateinit var resultBtn: Button
    private lateinit var imageView: ImageView
    private var bitmap: Bitmap? = null
    private var outputResult: String? = null
    private var roastResult: String = "Dump"
    private var getFile: File? = null
    private var uri: Uri? = null
    private lateinit var currentPhotoPath: String

    // Activity Result Launcher for selecting images
    private val getContent: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        handleImageSelection(uri)
    }

    // Constants
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI elements
        initializeViews()
        setupActionBar()
        requestPermissionsIfNeeded()

        val labels = loadLabels()

        selectBtn.setOnClickListener {
            selectImage()
        }

        resultBtn.setOnClickListener {
            processImage(labels)
        }
        takePictureBtn.setOnClickListener {
            startTakePhoto()
        }
    }

    // Request permissions if not granted
    private fun requestPermissionsIfNeeded() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // Check if all required permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initializeViews() {
        selectBtn = binding.buttonGallery
        takePictureBtn = binding.buttonCamera
        resultBtn = binding.buttonDetailResult
        imageView = binding.imageView
    }

    private fun loadLabels(): List<String> {
        return try {
            application.assets.open("labels.txt").bufferedReader().readLines()
        } catch (e: Exception) {
            Log.e("LabelsLoading", "Error loading labels", e)
            emptyList()
        }
    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@DetectionResultActivity,
                getString(R.string.tools_image_helper),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                // Update the 'bitmap' with the captured image
                bitmap = BitmapFactory.decodeFile(file.path)
                imageView.setImageBitmap(bitmap)
            }
            val uriConvert = Uri.fromFile(myFile)
            uri = uriConvert
        }
    }

    private fun handleImageSelection(uri: Uri?) {
        try {
            @Suppress("DEPRECATION")
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("ImageSelection", "Error handling image selection", e)
        }
    }

    private fun processImage(labels: List<String>) {
        val selectedBitmap = bitmap
        if (selectedBitmap != null) {
            val resizedBitmap = Bitmap.createScaledBitmap(selectedBitmap, 224, 224, true)
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            val model = MobileNetModelSeleksi.newInstance(this)
            val inputFeatureO =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val inputBuffer = tensorImage.buffer

            if (inputBuffer.capacity() != 224 * 224 * 3 * 4) {
                Log.e("ModelOutput", "Input buffer size does not match the expected size")
                return
            }

            inputFeatureO.loadBuffer(inputBuffer)

            val outputs = model.process(inputFeatureO)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
            val sum = outputFeature0.sum()

            var maxLabel = ""
            var maxPercentage = 0.0

            for (i in outputFeature0.indices) {
                val percentage = (outputFeature0[i] / sum) * 100.0
                if (percentage > maxPercentage) {
                    maxLabel = labels[i]
                    maxPercentage = percentage
                }
            }

            outputResult = maxLabel  // Store the label with the highest output

            val classResults = mutableListOf<Pair<String, Double>>()

            for (i in outputFeature0.indices) {
                val percentage = (outputFeature0[i] / sum) * 100.0
                classResults.add(Pair(labels[i], percentage))
            }

            classResults.sortByDescending { it.second }
            showDialog(this, classResults)
            model.close()
        } else {
            showNullImageSnackBar()
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.toolbar_detection)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showDialog(context: Context, resultData: List<Pair<String, Double>>): Dialog {
        val popupDialog = Dialog(this)
        val popupBinding = DialogResultBinding.inflate(layoutInflater)
        popupDialog.setContentView(popupBinding.root)

        val window = popupDialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.CENTER)

        val recyclerView = popupDialog.findViewById<RecyclerView>(R.id.dialogRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = DetectionResultAdapter(resultData)
        roastResult = when (outputResult) {
            "Light" -> {
                popupBinding.edPopupHoverButton.text = getString(R.string.title_light)
                getString(R.string.description_result_light)
            }
            "Medium" -> {
                popupBinding.edPopupHoverButton.text = getString(R.string.title_medium)
                getString(R.string.description_result_medium)
            }
            "Dark" -> {
                popupBinding.edPopupHoverButton.text = getString(R.string.title_dark)
                getString(R.string.description_result_dark)
            }
            else -> {
                getString(R.string.tools_null_data)
            }
        }

        // Set the OnClickListener for the button after the when expression
        popupBinding.edPopupHoverButton.setOnClickListener {
            val intentToDetail = Intent(this, DetailRoastingActivity::class.java)
            val selectedRoast: Roast? = when (outputResult) {
                "Light" -> getListRoast(this@DetectionResultActivity).getOrNull(0)
                "Medium" -> getListRoast(this@DetectionResultActivity).getOrNull(1)
                "Dark" -> getListRoast(this@DetectionResultActivity).getOrNull(2)
                else -> null
            }

            selectedRoast?.let {
                intentToDetail.putExtra("DATA", it)
                startActivity(intentToDetail)
            }
        }

        popupBinding.edPopupRoastInformation.text = roastResult

        popupBinding.edPopupCloseButton.setOnClickListener {
            popupDialog.dismiss()
        }

        popupDialog.show()
        return popupDialog
    }

    private fun showNullImageSnackBar() {
        snackBarAction(
            this@DetectionResultActivity,
            getString(R.string.tools_null_image),
            getString(R.string.menu_app_info),
            {
                showDialogInfo(
                    this@DetectionResultActivity,
                    getString(R.string.tools_info_detection_title),
                    getString(R.string.tools_info_detection_content),
                    false
                )
            }
        )
    }
}