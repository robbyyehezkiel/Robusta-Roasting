package com.robbyyehezkiel.robustaroasting.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Roast(
    val title: String,
    val description: String,
    val temperature: String,
    @DrawableRes val photoResId: Int,
    val color: String,
    val flavour: String,
    val aroma: String
) : Parcelable
