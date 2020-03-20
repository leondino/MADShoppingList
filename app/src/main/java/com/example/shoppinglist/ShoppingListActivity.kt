package com.example.shoppinglist

import android.os.Bundle
import android.telephony.mbms.MbmsErrors
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.android.synthetic.main.content_shopping_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val products = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(products, this)
    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.bar_title)

        productRepository = ProductRepository(this)
        initView()

        fab.setOnClickListener {
            addProduct()
        }
    }

    private fun initView(){
        rvProduct.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProduct.adapter = productAdapter
        rvProduct.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        createItemTouchHelper().attachToRecyclerView(rvProduct)
        getShoppingListFromDatabase()
    }

    private fun getShoppingListFromDatabase() {
        mainScope.launch {
            val shoppingList = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }
            this@ShoppingListActivity.products.clear()
            this@ShoppingListActivity.products.addAll(shoppingList)
            this@ShoppingListActivity.productAdapter.notifyDataSetChanged()
        }
    }

    private fun validateItem(): Boolean{
        return if (etProduct.text.toString().isNotBlank() && etQuantity.text.toString().isNotBlank()) {
            true
        } else {
            Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun addProduct(){
        if(validateItem()){
            mainScope.launch {
                val product = Product(etQuantity.text.toString().toInt(), etProduct.text.toString())
                withContext(Dispatchers.IO){
                    productRepository.insertProduct(product)
                }
                getShoppingListFromDatabase()
            }
        }
    }


    private fun createItemTouchHelper() : ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position =  viewHolder.adapterPosition
                val productToDelete = products[position]
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        productRepository.deleteProduct(productToDelete)
                    }
                    getShoppingListFromDatabase()
                }
            }

        }

        return ItemTouchHelper(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
