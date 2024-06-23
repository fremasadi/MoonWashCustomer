package com.dialiax.moonwash.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.dialiax.moonwash.R
import com.dialiax.moonwash.model.SpinnerItemPayments

class ItemPaymentSpinnerAdapter(context: Context, private val items: List<SpinnerItemPayments>) :
    ArrayAdapter<SpinnerItemPayments>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        val item = getItem(position)
        val imageView = view.findViewById<ImageView>(R.id.image)

        item?.let {
            imageView.setImageResource(it.imageResId)
        }

        return view
    }
}
