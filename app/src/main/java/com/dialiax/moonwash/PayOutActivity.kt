package com.dialiax.moonwash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dialiax.moonwash.databinding.ActivityPayOutBinding
import com.dialiax.moonwash.model.OrderDetails
import com.dialiax.moonwash.model.SpinnerItemPayments
import com.google.firebase.auth.FirebaseAuth
import com.dialiax.moonwash.adapter.ItemPaymentSpinnerAdapter
import com.google.firebase.database.*

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var itemsName: ArrayList<String>
    private lateinit var itemsPrice: ArrayList<String>
    private lateinit var itemsEstimasi: ArrayList<String>
    private lateinit var itemsDescription: ArrayList<String>
    private lateinit var itemsImage: ArrayList<String>
    private lateinit var itemsQuantity: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Set user data
        setUserData()

        // Get intent data
        val intent = intent
        itemsName = intent.getStringArrayListExtra("itemsName") as ArrayList<String>
        itemsPrice = intent.getStringArrayListExtra("itemsPrice") as ArrayList<String>
        itemsEstimasi = intent.getStringArrayListExtra("itemsEstimasi") as ArrayList<String>
        itemsDescription = intent.getStringArrayListExtra("itemsDescription") as ArrayList<String>
        itemsImage = intent.getStringArrayListExtra("itemsImage") as ArrayList<String>
        itemsQuantity = intent.getIntegerArrayListExtra("itemsQuantity") as ArrayList<Int>

        // Calculate and set total amount
        totalAmount = calculateTotalAmount().toString()
        binding.amount.isEnabled = false
        binding.amount.setText(totalAmount)

        // Back button listener
        binding.backButton.setOnClickListener {
            finish()
        }

        // Place order button listener
        binding.placeMyOrder.setOnClickListener {
            // Get data from text fields
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            // Validate input fields
            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Silakan Masukkan Semua Detailnya", Toast.LENGTH_SHORT).show()
            } else {
                // Check if user can place order
                canPlaceOrder()
            }
        }

        // Initialize payment method spinner
        val spinnerItems = listOf(
            SpinnerItemPayments(R.drawable.cashondelivery),
            SpinnerItemPayments(R.drawable.bca),
            SpinnerItemPayments(R.drawable.bri),
            SpinnerItemPayments(R.drawable.spay),
            SpinnerItemPayments(R.drawable.ovo),
        )
        val adapter = ItemPaymentSpinnerAdapter(this, spinnerItems)
        binding.spItem.adapter = adapter
    }

    private fun canPlaceOrder() {
        userId = auth.currentUser?.uid ?: ""

        // Check if there is an existing order
        databaseReference.child("user").child(userId).child("BuyHistory")
            .orderByChild("orderCompleted").equalTo(false)
            .limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            this@PayOutActivity,
                            "Anda tidak dapat memesan karena pesanan sebelumnya belum selesai",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // No existing incomplete order, allow placing order
                        placeOrder()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@PayOutActivity,
                        "Gagal memeriksa pesanan sebelumnya",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun placeOrder() {
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userId, name, itemsName, itemsImage, itemsPrice, itemsQuantity, address, totalAmount, phone,
            time, itemPushKey, false, false, false, false,false
        )

        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed Order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {
                // Enable place order button after successfully placing order
                binding.placeMyOrder.isEnabled = true
            }.addOnFailureListener {
                Toast.makeText(
                    this@PayOutActivity,
                    "Gagal menambahkan ke riwayat pembelian",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in itemsPrice.indices) {
            var price = itemsPrice[i]
            val lastChar = price.last()
            val priceIntValue = if (lastChar == '$') {
                price.dropLast(1).toInt()
            } else {
                price.toInt()
            }
            var quantity = itemsQuantity[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names = snapshot.child("name").getValue(String::class.java) ?: ""
                        val addresses = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phones = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
