package com.example.shoppinglist.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.model.Product
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(private val products: List<Product>, private val context: Context) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(product: Product){
            itemView.tvProduct.text =
                context.getString(R.string.product, product.quantity, product.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }
}