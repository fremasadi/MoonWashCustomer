package com.dialiax.moonwash.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dialiax.moonwash.R
import com.dialiax.moonwash.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val names: List<String>,
    private val prices: List<String>,
    private val images: List<String>,
    private val context: Context
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(names[position], prices[position], images[position])
    }

    override fun getItemCount(): Int = names.size

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, price: String, image: String) {
            binding.buyAgainName.text = name
            binding.buyAgainPrice.text = price
            Glide.with(context).load(image).into(binding.buyAgainImage)
        }
    }
}

