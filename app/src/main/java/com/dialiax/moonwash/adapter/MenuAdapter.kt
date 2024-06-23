package com.dialiax.moonwash.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dialiax.moonwash.DetailsActivity
import com.dialiax.moonwash.databinding.MenuItemBinding
import com.dialiax.moonwash.model.MenuItem

class MenuAdapter(
                  private val menuItems:List<MenuItem>,
                  private val requiredContext : Context
): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }

            }
        }

        // set data into recyclerview item name, price, image
        fun bind(position: Int) {
            val menuItem = menuItems[position]
                binding.apply {
                    menuName.text = menuItem.name
                    menuPrice.text = menuItem.price
                    val uri = Uri.parse(menuItem.image)
                    Glide.with(requiredContext).load(uri).into(menuImage)
                }
        }

    }

    private fun openDetailActivity(position: Int) {
        val menuItem = menuItems[position]

        // intent to open detail activity and pass data
        val intent = Intent(requiredContext,DetailsActivity::class.java).apply {
            putExtra("MenuItemName",menuItem.name)
            putExtra("MenuItemImage",menuItem.image)
            putExtra("MenuItemDescription",menuItem.description)
            putExtra("MenuItemIngredients",menuItem.estimasi)
            putExtra("MenuItemPrice",menuItem.price)
        }
        //start the detail activity
        requiredContext.startActivity(intent)
    }

    interface OnClickListener{
        fun onItemCLick(position: Int)
    }

}

