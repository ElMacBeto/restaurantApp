package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Extensions.Companion.loadImageUrl
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.databinding.ItemProductsBinding


class ProductsAdapter(
    private var dataSet: List<ProductListModel>,
    private val onClick: (ProductListModel) -> Unit
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun updateList(newList: List<ProductListModel>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ctx = view.context
        private val binding = ItemProductsBinding.bind(view)

        fun bindView(product: ProductListModel, isSelected:Boolean) {
            binding.product = product
            binding.imgProduct.loadImageUrl(product.imageUrl)
            if (isSelected)
                binding.container.setBackgroundColor(ctx.getColor(R.color.blue2))
            else
                binding.container.setBackgroundColor(ctx.getColor(R.color.white))
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
        val isSelected = viewHolder.adapterPosition == selectedPosition

        viewHolder.bindView(product, isSelected)
        viewHolder.itemView.setOnClickListener {
            selectedPosition = viewHolder.adapterPosition
            onClick.invoke(product)
        }
    }

    override fun getItemCount() = dataSet.size

}
