package com.example.earthquake.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GeoJson(
    val coordinates: List<Double>
)