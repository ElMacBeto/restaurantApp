package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.ItemOrderProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ProductsOrderDiffUtil

class ProductsOrderAdapter(
    private var dataSet: List<ProductsOrderModel>,
    private val onClick: (ProductsOrderModel) -> Unit
) :
    RecyclerView.Adapter<ProductsOrderAdapter.ViewHolder>() {


    fun updateList(newList: List<ProductsOrderModel>) {
        val projectDiffUtil = ProductsOrderDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ctx = view.context
        private val binding = ItemOrderProductBinding.bind(view)

        fun bindView(product: ProductsOrderModel) {
            binding.tvProductName.text = product.product
            binding.tvProductAmount.text = ctx.getString(R.string.label_text_amount_product, product.amount)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_order_product, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val product = dataSet[position]

        viewHolder.bindView(product)
        viewHolder.itemView.setOnClickListener {
            onClick.invoke(product)
        }
    }

    override fun getItemCount() = dataSet.size

}
