package com.example.earthquake.data.remote

import com.example.earthquake.model.KandilliResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KandilliApi {

    @GET("deprem/kandilli/archive")
    suspend fun getAllEarthquakes(
        @Query("start") startDate: String,   // örn: "2020-01-01"
        @Query("end") endDate: String        // örn: "2025-12-31"
    ): KandilliResponse
}
