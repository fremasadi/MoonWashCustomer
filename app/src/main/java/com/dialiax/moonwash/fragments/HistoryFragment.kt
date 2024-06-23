package com.dialiax.moonwash.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dialiax.moonwash.MainActivity
import com.dialiax.moonwash.RecentOrderItemsActivity
import com.dialiax.moonwash.adapter.BuyAgainAdapter
import com.dialiax.moonwash.databinding.FragmentHistoryBinding
import com.dialiax.moonwash.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()
    private var completedOrderItems: MutableList<OrderDetails> = mutableListOf()
    private var recentOrderItems: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // retrieve and display the user order history
        retrieveBuyHistory()

        // recent buy Button Click
        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }

        binding.receivedButton.setOnClickListener {
            checkOrderConditionsAndProceed()
        }

        return binding.root
    }

    private fun checkOrderConditionsAndProceed() {
        val recentOrder = recentOrderItems.firstOrNull()
        if (recentOrder != null) {
            val itemPushKey = recentOrder.itemPushKey
            val userReference = database.reference.child("user").child(userId).child("BuyHistory").child(itemPushKey!!)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val orderDetails = snapshot.getValue(OrderDetails::class.java)
                        if (orderDetails != null &&
                            orderDetails.orderAccepted == true &&
                            orderDetails.paymentReceived == true &&
                            orderDetails.orderExecute == true &&
                            orderDetails.orderDelivery == true
                        ) {
                            updateOrderStatus()
                        } else {
                            Toast.makeText(requireContext(), "Pesanan tidak memenuhi syarat untuk diterima.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Data pesanan tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                    Toast.makeText(requireContext(), "Terjadi kesalahan: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Data pesanan tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun updateOrderStatus() {
        val itemPushKey = recentOrderItems[0].itemPushKey
        if (itemPushKey != null) {
            val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey)
            completeOrderReference.child("paymentReceived").setValue(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Pesanan telah diterima, terima kasih!", Toast.LENGTH_SHORT).show()
                    updateOrderCompletionStatus(itemPushKey)
                } else {
                    Toast.makeText(requireContext(), "Gagal memperbarui status pembayaran.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Tidak dapat memperbarui status pesanan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOrderCompletionStatus(itemPushKey: String) {
        val userReference = database.reference.child("user").child(userId).child("BuyHistory").child(itemPushKey)
        val completedOrderReference = database.reference.child("CompletedOrder").child(itemPushKey)

        val updates = mapOf<String, Any>(
            "orderCompleted" to true
        )

        userReference.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completedOrderReference.updateChildren(updates).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        Toast.makeText(requireContext(), "Pesanan telah diperbarui sebagai selesai!", Toast.LENGTH_SHORT).show()
                        retrieveBuyHistory() // Refresh the data
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui pesanan di CompletedOrder", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Gagal memperbarui pesanan di BuyHistory", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // function to see items recent buy
    private fun seeItemsRecentBuy() {
        recentOrderItems.firstOrNull()?.let { recentBuy ->
            val arrayListOfOrderItem = ArrayList(recentOrderItems)
            val intent = Intent(requireContext(), RecentOrderItemsActivity::class.java)
            intent.putExtra("RecentBuyOrderItem", arrayListOfOrderItem)
            startActivity(intent)
        }
    }

    // function to retrieve items buy history
    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")

        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                completedOrderItems.clear()
                recentOrderItems.clear()
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                        if (it.orderCompleted == true) {
                            completedOrderItems.add(it)
                        } else {
                            recentOrderItems.add(it)
                        }
                    }
                }
                recentOrderItems.reverse()
                completedOrderItems.reverse()
                if (recentOrderItems.isNotEmpty()) {
                    // display the most recent order details
                    setDataInRecentBuyItem()
                }
                if (completedOrderItems.isNotEmpty() || recentOrderItems.isNotEmpty()) {
                    // setup the recyclerview with previous order details
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    // function to most recent order details
    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = recentOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainName.text = it.Names?.firstOrNull() ?: ""
                buyAgainPrice.text = it.Prices?.firstOrNull() ?: ""
                val image = it.Images?.firstOrNull() ?: ""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainImage)

                val isOrderIsAccepted = recentOrderItems[0].orderAccepted
                Log.d("TAG", "setDataInRecentBuyItem: $isOrderIsAccepted")
                if (isOrderIsAccepted) {
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility = View.VISIBLE
                }
            }
        }
    }

    // function to setup the recycler view
    private fun setPreviousBuyItemsRecyclerView() {
        val rv = binding.BuyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())

        buyAgainAdapter = BuyAgainAdapter(
            completedOrderItems.flatMap { it.Names ?: emptyList() },
            completedOrderItems.flatMap { it.Prices ?: emptyList() },
            completedOrderItems.flatMap { it.Images ?: emptyList() },
            requireContext()
        )

        rv.adapter = buyAgainAdapter
    }
}
