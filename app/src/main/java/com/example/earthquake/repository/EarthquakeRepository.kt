package com.example.earthquake.repository

import android.util.Log
import com.example.earthquake.model.KandilliItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.*

class EarthquakeRepository {

    suspend fun getEarthquakes(): List<KandilliItem> = withContext(Dispatchers.IO) {
        val url = "http://www.koeri.boun.edu.tr/scripts/lst9.asp"

        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get()

            val rawText = doc.select("pre").text()
            val rows = rawText.lines().drop(6)

            rows.mapNotNull { row ->
                try {
                    val parts = row.trim().split(Regex("\\s+"))
                    if (parts.size < 10) return@mapNotNull null

                    val date = parts[0] + " " + parts[1]
                    val lat = parts[2]
                    val lng = parts[3]
                    val magnitude = parts[6]
                    val location = parts.subList(9, parts.size).joinToString(" ")

                    KandilliItem(
                        title = location,
                        date = date,
                        magnitude = magnitude,
                        lat = lat,
                        lng = lng
                    )
                } catch (e: Exception) {
                    Log.e("EarthquakeRepository", "Satır parse hatası: ${e.message}")
                    null
                }
            }

        } catch (e: Exception) {
            Log.e("EarthquakeRepository", "Veri çekme hatası", e)
            emptyList()
        }
    }
}
