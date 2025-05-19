package com.example.earthquake.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquake.databinding.ItemEarthquakeBinding
import com.example.earthquake.databinding.ItemKandilliFooterBinding
import com.example.earthquake.model.KandilliItem

class EarthquakeAdapter(
    private var list: List<KandilliItem>,
    private val onItemClick: (KandilliItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    inner class EarthquakeViewHolder(val binding: ItemEarthquakeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KandilliItem) {
            binding.txtLocation.text = item.title
            binding.txtMagnitude.text = item.magnitude
            binding.txtTime.text = item.date

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    inner class FooterViewHolder(binding: ItemKandilliFooterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun getItemCount(): Int = list.size + 1 // +1 footer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemEarthquakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EarthquakeViewHolder(binding)
        } else {
            val binding = ItemKandilliFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EarthquakeViewHolder && position < list.size) {
            holder.bind(list[position])
        }
    }

    fun updateList(newList: List<KandilliItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
