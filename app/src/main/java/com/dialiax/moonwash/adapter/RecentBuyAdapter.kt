package com.dialiax.moonwash.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dialiax.moonwash.databinding.RecentbuyitemBinding

class RecentBuyAdapter(
    private val context: Context,
    private val NameList: ArrayList<String>,
    private val ImageList: ArrayList<String>,
    private val PriceList: ArrayList<String>,
    private val QuantityList: ArrayList<Int>,
    private var itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = RecentbuyitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = NameList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class RecentViewHolder(private val binding: RecentbuyitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
            }
        }

        fun bind(position: Int) {
            binding.apply {
                Name.text = NameList[position]
                Price.text = PriceList[position]
                Quantity.text = QuantityList[position].toString()
                val uriString = ImageList[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(Image)
            }
        }
    }
}