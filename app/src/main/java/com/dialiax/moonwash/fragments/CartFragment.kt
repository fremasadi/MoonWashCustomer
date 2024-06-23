package com.dialiax.moonwash.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dialiax.moonwash.PayOutActivity
import com.dialiax.moonwash.adapter.CartAdapter
import com.dialiax.moonwash.databinding.FragmentCartBinding
import com.dialiax.moonwash.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var Names: MutableList<String>
    private lateinit var Prices: MutableList<String>
    private lateinit var Descriptions: MutableList<String>
    private lateinit var ImagesUri: MutableList<String>
    private lateinit var Estimasi: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItems()


        binding.proceedbutton.setOnClickListener {
            //get order item details before proceeding to check out
            getOrderItemsDetails()
        }
        return binding.root
    }

    private fun getOrderItemsDetails() {
        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        val Name = mutableListOf<String>()
        val Image = mutableListOf<String>()
        val Price = mutableListOf<String>()
        val Description = mutableListOf<String>()
        val Estimasi = mutableListOf<String>()
        val Quantity = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cartITerms to respective list
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    //add items details in to list
                    orderItems?.Name?.let { Name.add(it) }
                    orderItems?.Price?.let { Price.add(it) }
                    orderItems?.Description?.let { Description.add(it) }
                    orderItems?.Image?.let { Image.add(it) }
                    orderItems?.Estimasi?.let { Estimasi.add(it) }
                }
                orderNow(
                    Name,
                    Price,
                    Estimasi,
                    Description,
                    Image,
                    Quantity
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Pembuatan pesanan Gagal. Silakan Coba Lagi",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun orderNow(
        Name: MutableList<String>,
        Price: MutableList<String>,
        Estimasi: MutableList<String>,
        Description: MutableList<String>,
        Image: MutableList<String>,
        Quantity: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("itemsName", Name as ArrayList<String>)
            intent.putExtra("itemsPrice", Price as ArrayList<String>)
            intent.putExtra("itemsEstimasi", Estimasi as ArrayList<String>)
            intent.putExtra("itemsDescription", Description as ArrayList<String>)
            intent.putExtra("itemsImage", Image as ArrayList<String>)
            intent.putExtra("itemsQuantity", Quantity as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        // database reference to firebase
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        //list to store cart items
        Names = mutableListOf()
        Prices = mutableListOf()
        Descriptions = mutableListOf()
        ImagesUri = mutableListOf()
        Estimasi = mutableListOf()
        quantity = mutableListOf()

        //fetch data form database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cart items object from child node
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    //add cart item details to list
                    cartItems?.Name?.let { Names.add(it) }
                    cartItems?.Price?.let { Prices.add(it) }
                    cartItems?.Description?.let { Descriptions.add(it) }
                    cartItems?.Image?.let { ImagesUri.add(it) }
                    cartItems?.Quantity?.let { quantity.add(it) }
                    cartItems?.Estimasi?.let { Estimasi.add(it) }
                }

                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data tidak diambil", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(
            requireContext(),
            Names,
            Prices,
            ImagesUri,
            Descriptions,
            quantity,
            Estimasi
        )
        binding.cartRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRecyclerView.adapter = cartAdapter
    }

    companion object {

    }
}