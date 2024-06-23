package com.dialiax.moonwash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dialiax.moonwash.adapter.NotificationAdapter
import com.dialiax.moonwash.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotificationBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBottomBinding.inflate(layoutInflater, container, false)
        val notification = listOf("Pesanan Lama Sekali Diprosesnya","Pesanan cepat prosesnya","Bagus Pelayanannya")
        val notificationImages = listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.congrats)

        val adapter = NotificationAdapter(
            ArrayList(notification),
            ArrayList(notificationImages)
        )
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter  = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Here you can set up your views or perform any other initialization
    }

    companion object {
        // You can add companion object functions or variables here
    }
}
