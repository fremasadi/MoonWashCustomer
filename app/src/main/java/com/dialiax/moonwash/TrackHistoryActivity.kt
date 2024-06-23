package com.dialiax.moonwash

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dialiax.moonwash.databinding.ActivityTrackHistoryBinding
import com.dialiax.moonwash.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TrackHistoryActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityTrackHistoryBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Dapatkan userId
        userId = auth.currentUser?.uid ?: ""

        // Inisialisasi database Firebase dengan userId
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userId).child("BuyHistory")

        // Jalankan fungsi untuk melacak status pesanan
        trackOrderStatus()
    }

    private fun trackOrderStatus() {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderSnapshot = snapshot.children.first() // Ambil data paling belakang

                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)

                    // Pastikan orderDetails tidak null
                    orderDetails?.let {
                        val orderAccepted = it.orderAccepted
                        val paymentReceived = it.paymentReceived
                        val orderExecute:Boolean = it.orderExecute
                        val orderCompleted : Boolean = it.orderCompleted

                        // Periksa jika order telah diterima oleh admin
                        if (orderAccepted) {
                            binding.orderStatusReceived.background.setTint(Color.GREEN)

                        }

                        if (paymentReceived) {
                            binding.orderStatusPickedUp.background.setTint(Color.GREEN)
                            binding.orderStatusReceived.background.setTint(Color.LTGRAY)
                        }

                        if (orderExecute){
                            binding.orderStatusInProgress.background.setTint(Color.GREEN)
                            binding.orderStatusPickedUp.background.setTint(Color.LTGRAY)
                        }

                        if (orderCompleted){
                            binding.orderStatusCompleted.background.setTint(Color.GREEN)
                            binding.orderStatusInProgress.background.setTint(Color.LTGRAY)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle jika terjadi error
            }
        })
    }

}
