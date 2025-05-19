package com.example.earthquake.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.earthquake.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(
    private val onFilterSelected: (Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.option1Day.setOnClickListener {
            onFilterSelected(1)
            dismiss()
        }

        binding.option10Days.setOnClickListener {
            onFilterSelected(10)
            dismiss()
        }

        binding.option1Month.setOnClickListener {
            onFilterSelected(30)
            dismiss()
        }

        binding.option1Year.setOnClickListener {
            onFilterSelected(365)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
