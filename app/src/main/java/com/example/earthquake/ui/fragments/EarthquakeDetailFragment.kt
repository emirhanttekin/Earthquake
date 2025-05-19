package com.example.earthquake.ui.fragments

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.earthquake.R
import com.example.earthquake.databinding.FragmentEarthquakeDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class EarthquakeDetailFragment : Fragment() {

    private var _binding: FragmentEarthquakeDetailBinding? = null
    private val binding get() = _binding!!
    private val args: EarthquakeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEarthquakeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val earthquake = args.earthquake

        val lat = earthquake.lat?.toDoubleOrNull()
        val lng = earthquake.lng?.toDoubleOrNull()
        val magnitude = earthquake.magnitude?.toDoubleOrNull()

        if (lat == null || lng == null) {
            Log.e("EarthquakeDetail", "Koordinatlar eksik: lat=$lat, lng=$lng")
            return
        }

        val latLng = LatLng(lat, lng)

        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(binding.mapContainer.id, mapFragment)
            .commit()

        mapFragment.getMapAsync { googleMap ->
            try {
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.dark_map_style)
                )
            } catch (e: Exception) {
                Log.e("MapStyle", "Harita stili uygulanamadı: ${e.message}")
            }

            if (magnitude != null && magnitude >= 4.0) {
                val starBitmap = vectorToBitmap(R.drawable.star_quake, 64, 64)
                if (starBitmap != null) {
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(starBitmap))
                            .anchor(0.5f, 0.5f)
                            .title("Büyüklük: $magnitude")
                    )
                }
            } else {
                googleMap.addCircle(
                    CircleOptions()
                        .center(latLng)
                        .radius(1500.0)
                        .strokeColor(Color.RED)
                        .strokeWidth(4f)
                        .fillColor(Color.RED)
                )
            }

            startPulseAnimation(googleMap, latLng)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
        }
    }

    private fun startPulseAnimation(googleMap: com.google.android.gms.maps.GoogleMap, latLng: LatLng) {
        val pulseCircle = googleMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(0.0)
                .strokeColor(Color.parseColor("#FF4500"))
                .strokeWidth(2f)
                .fillColor(Color.argb(60, 255, 69, 0))
        )

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 2000L

            addUpdateListener { animation ->
                val fraction = animation.animatedFraction
                val radius = 5000 + (fraction * 25000)
                val alpha = (1 - fraction) * 40
                pulseCircle.radius = radius.toDouble()
                pulseCircle.fillColor = Color.argb(alpha.toInt(), 255, 69, 0)
            }
        }
        animator.start()
    }

    private fun vectorToBitmap(drawableId: Int, width: Int = 64, height: Int = 64): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

