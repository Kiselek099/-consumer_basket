package com.example.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var mainTB: Toolbar
    lateinit var db: DBHelper
    lateinit var nameET: EditText
    lateinit var weightET: EditText
    lateinit var priceET: EditText
    lateinit var saveBTN: Button
    lateinit var updateBTN: Button
    lateinit var delBTN: Button
    lateinit var productsLV: ListView
    lateinit var myListAdapter: MyListAdapter
    var productList: MutableList<Product> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DBHelper(this, null)
        nameET = findViewById(R.id.nameET)
        weightET = findViewById(R.id.weightET)
        priceET = findViewById(R.id.priceET)
        saveBTN = findViewById(R.id.saveBTN)
        productsLV = findViewById(R.id.productsLV)
        mainTB = findViewById(R.id.mainTB)
        updateBTN = findViewById(R.id.updateBTN)
        delBTN = findViewById(R.id.delBTN)
        setSupportActionBar(mainTB)
        title = "Потребительская корзина"
        myListAdapter = MyListAdapter(this, productList)
        productsLV.adapter = myListAdapter
        saveBTN.setOnClickListener {
            val productName = nameET.text.toString()
            val productWeight = weightET.text.toString()
            val productPrice = priceET.text.toString()
            if (productName.isNotEmpty() && productWeight.isNotEmpty() && productPrice.isNotEmpty()) {
                val product = Product(productName, productWeight, productPrice)
                db.addName(product)
                loadProducts()
                nameET.text.clear()
                weightET.text.clear()
                priceET.text.clear()
            }
        }
    }

    private fun loadProducts() {
        productList.clear()
        productList.addAll(db.readProduct())
        myListAdapter.notifyDataSetChanged()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.exitApp -> {
                finishAffinity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
