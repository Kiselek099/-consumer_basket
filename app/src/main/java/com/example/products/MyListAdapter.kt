package com.example.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyListAdapter (private val context: Context, productList:MutableList<Product>):
ArrayAdapter<Product>(context,R.layout.list_item,productList){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val product=getItem(position)
        var view=convertView
        if(view==null){
            view=LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
        }
        val nameText=view?.findViewById<TextView>(R.id.nameTV)
        val weightText=view?.findViewById<TextView>(R.id.weightTV)
        val priceText=view?.findViewById<TextView>(R.id.priceTV)
        nameText?.text = "Name: ${product?.name}"
        weightText?.text = "Weight: ${product?.weight}"
        priceText?.text = "Price: ${product?.price}"
        return view!!

    }
}