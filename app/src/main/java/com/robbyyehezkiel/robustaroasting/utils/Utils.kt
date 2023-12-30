package com.robbyyehezkiel.robustaroasting.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Roast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
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

fun getListRoast(context: Context): List<Roast> {
    val dataTitle = context.resources.getStringArray(R.array.roast_title)
    val dataDescription = context.resources.getStringArray(R.array.roast_description)
    val dataTemperature = context.resources.getStringArray(R.array.roast_temperature)
    val dataLogo = context.resources.obtainTypedArray(R.array.roast_photo)
    val dataColor = context.resources.getStringArray(R.array.roast_color)
    val dataFlavour = context.resources.getStringArray(R.array.roast_flavour)
    val dataAroma = context.resources.getStringArray(R.array.roast_aroma)
    val dataAgtron = context.resources.getStringArray(R.array.roast_agtron)
    val dataCoffee = context.resources.getStringArray(R.array.roast_coffee)
    val dataTitlePopup = context.resources.getStringArray(R.array.roast_process_title)
    val dataSubTitlePopup = context.resources.getStringArray(R.array.roast_process_subtitle)
    val popupLogo = context.resources.obtainTypedArray(R.array.roast_photo_pop_up)

    return dataTitle.indices.map { i ->
        Roast(
            dataTitle[i],
            dataDescription[i],
            dataTemperature[i],
            dataLogo.getResourceId(i, -1),
            dataColor[i],
            dataFlavour[i],
            dataAroma[i],
            dataAgtron[i],
            dataCoffee[i],
            dataTitlePopup[i],
            dataSubTitlePopup[i],
            popupLogo.getResourceId(i, -1),
        )
    }.also {
        dataLogo.recycle()
        popupLogo.recycle()
    }
}