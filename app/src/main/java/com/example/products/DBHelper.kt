package com.example.products

import android.annotation.SuppressLint
import android.app.Person
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQuery
import kotlin.coroutines.coroutineContext

class DBHelper(context: Context, factory: CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "PERSON_DATABASE"
        private val DATABASE_VERSION = 2
        val TABLE_NAME = "product_table"
        val KEY_NAME = "name"
        val KEY_WEIGHT = "weight"
        val KEY_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " (" +
                KEY_NAME + " TEXT, " +
                KEY_WEIGHT + " TEXT, " +
                KEY_PRICE + " TEXT" + ")")
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addName(product: Product) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, product.name)
        values.put(KEY_WEIGHT, product.weight)
        values.put(KEY_PRICE, product.price)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun readProduct(): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var productName: String
        var productWeight: String
        var productPrice: String
        if (cursor!!.moveToFirst()) {
            do {
                val productName=cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val productWeight = cursor.getString(cursor.getColumnIndex(KEY_WEIGHT))
                val productPrice = cursor.getString(cursor.getColumnIndex(KEY_PRICE))
                val product = Product(productName, productWeight, productPrice)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return productList
    }
    fun updatePerson(product: Product){
        val db=this.writableDatabase
        val contextValues= ContentValues()
        contextValues.put(KEY_NAME,product.name)
        contextValues.put(KEY_WEIGHT,product.weight)
        contextValues.put(KEY_PRICE,product.price)
        db.update(TABLE_NAME, contextValues, "name = ?", arrayOf(product.name))
        db.close()
    }
    fun deleteProduct(product: Product){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(KEY_NAME,product.name)
        db.delete(TABLE_NAME, "name = ?", arrayOf(product.name))
        db.close()
    }
}