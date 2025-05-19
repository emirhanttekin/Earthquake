package com.example.earthquake.model

import java.io.Serializable

data class KandilliItem(
    val title: String?,
    val date: String?,
    val magnitude: String?,
    val lat: String?,
    val lng: String?
) : Serializable
