package com.robbyyehezkiel.robustaroasting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val menuId: String,
    val name: String,
    val photo: Int,
) : Parcelable
