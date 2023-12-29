package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Extensions.Companion.loadImageUrl
import com.humbjorch.restaurantapp.data.model.HamburgerModel
import com.humbjorch.restaurantapp.databinding.ItemProductsBinding


class ProductsAdapter(
    private var dataSet: List<HamburgerModel>,
    private val onClick: (HamburgerModel, String) -> Unit
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    fun updateList(newList: List<HamburgerModel>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ctx = view.context
        private val binding = ItemProductsBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(product: HamburgerModel) {
            binding.product = product
            binding.imgProduct.loadImageUrl(product.image)
            if (product.isClicked){
                binding.container.setBackgroundColor(ctx.getColor(R.color.blue2))
            }else{
                binding.container.setBackgroundColor(ctx.getColor(R.color.white))
            }
        }
        fun getIngredient(): String{
            return "con todo"
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_products, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val product = dataSet[position]

        viewHolder.bindView(product)
        viewHolder.itemView.setOnClickListener {
            val ingredient = viewHolder.getIngredient()
            onClick.invoke(product, ingredient)
        }
    }

    override fun getItemCount() = dataSet.size

}
