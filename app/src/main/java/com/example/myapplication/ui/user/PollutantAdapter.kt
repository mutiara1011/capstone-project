package com.example.myapplication.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPollutantSectionBinding

class PollutantAdapter(private val pollutants: List<PollutantData>) :
    RecyclerView.Adapter<PollutantAdapter.PollutantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollutantViewHolder {
        val binding = ItemPollutantSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PollutantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollutantViewHolder, position: Int) {
        val pollutant = pollutants[position]
        holder.bind(pollutant)
    }

    override fun getItemCount(): Int = pollutants.size

    class PollutantViewHolder(private val binding: ItemPollutantSectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pollutant: PollutantData) {
            binding.textIndex.text = pollutant.index
            binding.textDescription.text = pollutant.description
            binding.textPollutant.text = pollutant.title
            binding.textConcentration.text = pollutant.details
        }
    }
}
