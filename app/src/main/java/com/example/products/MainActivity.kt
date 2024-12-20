package com.example.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

        setSupportActionBar(mainTB)
        title = "Потребительская корзина"
        myListAdapter = MyListAdapter(this, productList)
        productsLV.adapter = myListAdapter
        saveBTN.setOnClickListener {
            saveProduct()
        }
        productsLV.setOnItemClickListener { _, _, position, _ ->
            val product = productList[position]
            showProductActionsDialog(product, position)
        }
        viewDataAdapter()
    }

    private fun saveProduct() {
        val productName = nameET.text.toString()
        val productWeight = weightET.text.toString()
        val productPrice = priceET.text.toString()
        if (productName.isNotEmpty() && productWeight.isNotEmpty() && productPrice.isNotEmpty()) {
            val product = Product(productName, productWeight, productPrice)
            db.addProduct(product)
            productList.add(product)
            Toast.makeText(applicationContext, "Запись сохранена", Toast.LENGTH_SHORT).show()
            nameET.text.clear()
            weightET.text.clear()
            priceET.text.clear()
            viewDataAdapter()
        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        productList.clear()
        productList.addAll(db.readProduct())
        myListAdapter.notifyDataSetChanged()
    }

    private fun viewDataAdapter() {
        productList = db.readProduct()
        myListAdapter = MyListAdapter(this, productList)
        productsLV.adapter = myListAdapter
    }

    private fun updateProduct(product: Product, position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editName = dialogView.findViewById<EditText>(R.id.updateNameET)
        val editWeight = dialogView.findViewById<EditText>(R.id.updateWeightET)
        val editPrice = dialogView.findViewById<EditText>(R.id.updatePriceET)

        editName.setText(product.name)
        editWeight.setText(product.weight)
        editPrice.setText(product.price)

        dialogBuilder.setPositiveButton("Сохранить") { _, _ ->
            val updatedName = editName.text.toString()
            val updatedWeight = editWeight.text.toString()
            val updatedPrice = editPrice.text.toString()

            if (updatedName.isNotEmpty() && updatedWeight.isNotEmpty() && updatedPrice.isNotEmpty()) {
                val updatedProduct = Product(updatedName, updatedWeight, updatedPrice)
                db.updateProduct(updatedProduct)
                productList[position] = updatedProduct
                myListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Продукт обновлен", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Отмена", null)
        dialogBuilder.show()
    }

    private fun deleteProduct(product: Product, position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Вы уверены, что хотите удалить этот продукт?")
            .setPositiveButton("Удалить") { _, _ ->
                db.deleteProduct(product)
                productList.removeAt(position)
                myListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Продукт удален", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showProductActionsDialog(product: Product, position: Int) {
        val options = arrayOf("Обновить", "Удалить", "Отмена")

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setItems(options) { _, which ->
            when (which) {
                0 -> updateProduct(product, position)
                1 -> deleteProduct(product, position)
                else -> {}
            }
        }
        dialogBuilder.show()
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