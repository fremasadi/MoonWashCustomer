package com.dialiax.moonwash

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dialiax.moonwash.databinding.ActivityDetailsBinding
import com.dialiax.moonwash.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var name: String? = null
    private var price: String? = null
    private var description: String? = null
    private var image: String? = null
    private var estimasi: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth= FirebaseAuth.getInstance()

        name = intent.getStringExtra("MenuItemName")
        description = intent.getStringExtra("MenuItemDescription")
        estimasi = intent.getStringExtra("MenuItemIngredients")
        price = intent.getStringExtra("MenuItemPrice")
        image = intent.getStringExtra("MenuItemImage")

        with(binding){
            detailName.text = name
            detailDescription.text = description
            detailIngrediant.text = estimasi
            Glide.with(this@DetailsActivity).load(Uri.parse(image)).into(detailImage)

        }

        binding.imageButton2.setOnClickListener {
            finish()
        }
        binding.addtocartbutton.setOnClickListener {
            addItemToCart()
        }

    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?:""

        //create a cart Items object
        val cartItem = CartItems(name.toString(),price.toString(),description.toString(),image.toString(),1)
        
        //save data to cart item to firebase
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Item Berhasil Ditambahkan ke cart 😁", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Item Tidak Ditambahkan😒", Toast.LENGTH_SHORT).show()
        }
    }
}