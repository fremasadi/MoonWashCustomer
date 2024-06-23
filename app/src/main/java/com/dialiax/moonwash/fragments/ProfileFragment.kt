package com.dialiax.moonwash.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dialiax.moonwash.LoginActivity
import com.dialiax.moonwash.SignActivity
import com.dialiax.moonwash.Splach_Screen
import com.dialiax.moonwash.databinding.FragmentProfileBinding
import com.dialiax.moonwash.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phoneNo.isEnabled = false

        var isEnable = false

        binding.profileEdit.setOnClickListener {
            isEnable = !isEnable

            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phoneNo.isEnabled = isEnable

            if (isEnable) {
                binding.name.requestFocus()
            }
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), Splach_Screen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setUserData()

        binding.saveInfoButton.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val address = binding.address.text.toString()
            val phone = binding.phoneNo.text.toString()

            // Memeriksa apakah semua kolom diisi dan email mengandung @gmail.com
            if (name.isNotEmpty() && email.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                if (email.contains("@gmail.com")) {
                    updateUserData(name, email, address, phone)

                    binding.name.isEnabled = false
                    binding.address.isEnabled = false
                    binding.email.isEnabled = false
                    binding.phoneNo.isEnabled = false
                } else {
                    Toast.makeText(requireContext(), "Email harus mengandung @gmail.com", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        if (name.isNotEmpty() && email.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val userReference = database.getReference("user").child(userId)

                val userData = hashMapOf(
                    "name" to name,
                    "address" to address,
                    "email" to email,
                    "phone" to phone,
                )
                userReference.setValue(userData).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profil Update Berhasil", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Profil Update Gagal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null){
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null){
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.email.setText(userProfile.email)
                            binding.phoneNo.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        }
    }

}