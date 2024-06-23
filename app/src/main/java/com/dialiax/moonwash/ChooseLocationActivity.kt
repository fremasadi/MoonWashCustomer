package com.dialiax.moonwash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.dialiax.moonwash.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {

    private val binding : ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationList = arrayOf("Kediri Kota","Mojoroto","Pesantren",)

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)

        val autoCompleteTextView = binding.listoflocation
        autoCompleteTextView.setAdapter(adapter)
    }
}