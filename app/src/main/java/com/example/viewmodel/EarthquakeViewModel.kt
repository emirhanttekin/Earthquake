package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.earthquake.model.KandilliItem
import com.example.earthquake.repository.EarthquakeRepository
import com.example.earthquake.util.NotificationHelper
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EarthquakeRepository()

    private val _allEarthquakes = MutableLiveData<List<KandilliItem>>()   // 🔒 Tüm veriler
    private val _earthquakes = MutableLiveData<List<KandilliItem>>()      // 🔍 Ekranda gösterilen
    val earthquakes: LiveData<List<KandilliItem>> get() = _earthquakes

    private var pollingJob: Job? = null
    private var lastEarthquakeId: String? = null // ✅ Yeni geleni yakalamak için

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchAndShowToday()
                delay(60_000L)
            }
        }
    }

    private suspend fun fetchAndShowToday() {
        try {
            val latest = repository.getEarthquakes()
            _allEarthquakes.postValue(latest)

            if (latest.isNotEmpty()) {
                val newest = latest.first()
                val quakeId = "${newest.date}_${newest.title}"

                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
                val quakeTime = sdf.parse(newest.date ?: "")?.time ?: 0L
                val now = System.currentTimeMillis()

                val timeDiffMinutes = (now - quakeTime) / (60 * 1000)

                if (lastEarthquakeId == null) {

                    lastEarthquakeId = quakeId
                } else if (quakeId != lastEarthquakeId && timeDiffMinutes <= 2) {

                    lastEarthquakeId = quakeId
                    NotificationHelper.showNotification(
                        context = getApplication(),
                        title = "Yeni Deprem: ${newest.title.orEmpty()}",
                        message = "Büyüklük: ${newest.magnitude.orEmpty()}"
                    )
                }
            }

            _earthquakes.postValue(filterToday(latest))

        } catch (e: Exception) {
            Log.e("EarthquakeVM", "Veri çekme hatası", e)
        }
    }

    private fun filterToday(list: List<KandilliItem>): List<KandilliItem> {
        val sdfFull = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
        val sdfDay = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val today = sdfDay.format(Date())

        return list.filter {
            try {
                val quakeDate = sdfDay.format(sdfFull.parse(it.date ?: "") ?: return@filter false)
                quakeDate == today
            } catch (_: Exception) {
                false
            }
        }
    }

    fun filterEarthquakesByDays(days: Int) {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
        val thresholdDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }.time

        val filtered = _allEarthquakes.value?.filter {
            try {
                val quakeDate = sdf.parse(it.date ?: "") ?: return@filter false
                quakeDate.after(thresholdDate)
            } catch (_: Exception) {
                false
            }
        }

        _earthquakes.value = filtered ?: emptyList()
    }
    fun refreshOnceManually() {
        viewModelScope.launch {
            try {
                val latest = repository.getEarthquakes()
                _allEarthquakes.postValue(latest)

                val todayList = latest.filter { item ->
                    try {
                        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                        val quakeDate = sdf.format(SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).parse(item.date ?: ""))
                        val today = sdf.format(Date())
                        quakeDate == today
                    } catch (e: Exception) {
                        false
                    }
                }

                _earthquakes.postValue(todayList)

            } catch (e: Exception) {
                Log.e("EarthquakeVM", "Manuel yenileme hatası", e)
            }
        }
    }
    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }
}
