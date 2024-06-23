package com.dialiax.moonwash.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.dialiax.moonwash.MenuBottomSheetFragment
import com.dialiax.moonwash.R
import com.dialiax.moonwash.adapter.MenuAdapter
import com.dialiax.moonwash.databinding.FragmentHomeBinding
import com.dialiax.moonwash.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }

        //retrieve and display popular item
        retrieveAndDisplayPopularItems()

        return binding.root
    }

    private fun retrieveAndDisplayPopularItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve menu items from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //loop for through each food item
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    private fun randomPopularItems() {
        //create as shuffled list of menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] }

        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItems,requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val imageSlider = binding.imageSlider

        // Retrieve image URLs from Firebase
        val imageList = ArrayList<SlideModel>()

        val databaseReference = database.reference.child("imagePromo")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (imageSnapshot in snapshot.children) {
                    val imageUrl = imageSnapshot.getValue(String::class.java)
                    imageUrl?.let {
                        // Create SlideModel with the retrieved image URL
                        val slideModel = SlideModel(it, ScaleTypes.FIT)
                        imageList.add(slideModel)
                    }
                }

                // Set image list to imageSlider
                imageSlider.setImageList(imageList)

                // Set item click listener
                imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun doubleClick(position: Int) {
                        // Handle double click event
                    }

                    override fun onItemSelected(position: Int) {
                        val itemMessage = "Selected Image $position"
                        Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e(TAG, "Failed to retrieve image URLs: ${error.message}")
            }
        })
    }



}
