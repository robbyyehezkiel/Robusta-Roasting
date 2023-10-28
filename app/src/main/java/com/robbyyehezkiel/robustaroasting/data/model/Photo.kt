package com.robbyyehezkiel.robustaroasting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String? = null,
    val photoUrl: String? = null
) : Parcelable
