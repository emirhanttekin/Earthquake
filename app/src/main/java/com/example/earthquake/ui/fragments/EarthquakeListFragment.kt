package com.example.earthquake.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthquake.R
import com.example.earthquake.databinding.FragmentEarthquakeDetailBinding
import com.example.earthquake.databinding.FragmentEarthquakeListBinding
import com.example.earthquake.ui.adapter.EarthquakeAdapter
import com.example.earthquake.ui.filters.FilterBottomSheet
import com.example.earthquake.util.NotificationHelper
import com.example.viewmodel.EarthquakeViewModel


class EarthquakeListFragment : Fragment() {
    private var _binding : FragmentEarthquakeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EarthquakeAdapter
    private val viewModel: EarthquakeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentEarthquakeListBinding.inflate(inflater ,container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerQuakes.layoutManager = LinearLayoutManager(requireContext())
        adapter = EarthquakeAdapter(emptyList()) { selectedItem ->

            val action = EarthquakeListFragmentDirections
                .actionEarthquakeListFragmentToEarthquakeDetailFragment(selectedItem)
            findNavController().navigate(action)
        }

        binding.btnFilter.setOnClickListener {
            val filterSheet = FilterBottomSheet { dayCount ->
                viewModel.filterEarthquakesByDays(dayCount)
            }
            filterSheet.show(parentFragmentManager, "FilterBottomSheet")
        }

        binding.recyclerQuakes.adapter = adapter


        viewModel.earthquakes.observe(viewLifecycleOwner) { list ->
            Log.d("EarthquakeListFragment", "Earthquake count: ${list.size}")
            adapter.updateList(list)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // ✅ Swipe-to-refresh mantığı
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshOnceManually()
        }


    }





}