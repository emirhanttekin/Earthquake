package com.example.earthquake.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geometry(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("coordinates")
    val coordinates: List<Double>? = null
) : Parcelable
