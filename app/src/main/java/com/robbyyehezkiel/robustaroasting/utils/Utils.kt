package com.robbyyehezkiel.robustaroasting.utils

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Photo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAXIMAL_SIZE = 1000000

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateFile(file: File, isBackCamera: Boolean = false) {
    val matrix = Matrix()
    val bitmap = BitmapFactory.decodeFile(file.path)
    val rotation = if (isBackCamera) 90f else -90f
    matrix.postRotate(rotation)
    if (!isBackCamera) {
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun snackBarShort(view: View, snackBarText: String) {
    Snackbar.make(view, snackBarText, Snackbar.LENGTH_SHORT).show()
}

fun snackBarAction(
    activity: Activity, message: String, action: String? = null,
    actionListener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT
) {
    val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, duration)
    if (action != null && actionListener != null) {
        snackBar.setAction(action, actionListener)
    }
    snackBar.show()
}

fun showDialogInfo(
    context: Context,
    textDialogTitle: String,
    textDialogContent: String,
    justificationMode: Boolean? = false
) {
    val dialog = Dialog(context)
    dialog.setCancelable(true)
    dialog.window!!.apply {
        val params: WindowManager.LayoutParams = this.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.windowAnimations = android.R.transition.slide_bottom
        setGravity(Gravity.CENTER)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    dialog.setContentView(R.layout.preview_dialog_info)
    val tvTitle: TextView = dialog.findViewById(R.id.ed_preview_dialog_info_title)
    val tvContent: TextView = dialog.findViewById(R.id.ed_preview_dialog_info_content)
    tvTitle.text = textDialogTitle
    tvContent.text = textDialogContent
    val btnClose = dialog.findViewById<MaterialButton>(R.id.ed_preview_dialog_close)
    btnClose.setOnClickListener {
        dialog.dismiss()
    }
    if (justificationMode == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        tvContent.justificationMode = JUSTIFICATION_MODE_INTER_WORD
    }
    dialog.show()
}

fun openSettingPermission(context: Context) {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", context.packageName, null)
    context.startActivity(intent)
}