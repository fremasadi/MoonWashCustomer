package com.dialiax.moonwash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dialiax.moonwash.adapter.RecentBuyAdapter
import com.dialiax.moonwash.databinding.ActivityRecentOrderItemsBinding
import com.dialiax.moonwash.model.OrderDetails

class RecentOrderItemsActivity : AppCompatActivity() {

    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allNames: ArrayList<String>
    private lateinit var allImages: ArrayList<String>
    private lateinit var allPrices: ArrayList<String>
    private lateinit var allQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }


        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>

        // Handle potential empty list
        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                // Process the list (choose one of the options below):

                // Option 1: Iterate through all items
                for (orderItem in orderDetails) {
                    allNames = orderItem.Names as ArrayList<String>
                    allImages = orderItem.Images as ArrayList<String>
                    allPrices = orderItem.Prices as ArrayList<String>
                    allQuantities = orderItem.Quantities as ArrayList<Int>
                    setAdapter() // Call setAdapter after processing each item (if applicable)
                }

            } else {
                Toast.makeText(this, "Daftar Kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setAdapter() {
        val rv = binding.recentBuyRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allNames, allImages, allPrices, allQuantities)
        adapter.setOnItemClickListener(object : RecentBuyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle item click event here
                val intent = Intent(this@RecentOrderItemsActivity, TrackHistoryActivity::class.java)
                // Add any data you want to pass to the TrackHistoryActivity
                startActivity(intent)
            }
        })
        rv.adapter = adapter
    }

}