package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.ProductActionListener
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.ItemOrderProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ProductsOrderDiffUtil

class ProductsOrderAdapter(
    private var dataSet: List<ProductsOrderModel>,
    private val onClick: ((ProductsOrderModel, ProductActionListener) -> Unit)? = null
) :
    RecyclerView.Adapter<ProductsOrderAdapter.ViewHolder>() {

    var hideButtons: Boolean = false

    fun updateList(newList: List<ProductsOrderModel>) {
        val projectDiffUtil = ProductsOrderDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ctx = view.context
        val binding = ItemOrderProductBinding.bind(view)


        fun bindView(product: ProductsOrderModel) {
            binding.tvProductName.text = product.product
            binding.tvProductAmount.text =
                ctx.getString(R.string.label_text_amount_product, product.amount.toInt())
            val totalPrice = product.amount.toInt() * (product.price.toInt() + product.extras.sumOf { it.price.toInt() })
            binding.tvPrice.text = ctx.getString(R.string.label_price_product, totalPrice)

            val ingredientText = StringBuilder()
            ingredientText.append(product.ingredients.joinToString())

            if (product.extras.isNotEmpty()){
                ingredientText.append("\nextras: " + product.extras.joinToString { it.name })
            }
            binding.tvIngredients.text = ingredientText
        }

        fun hideOrShowButtons(hide:Boolean){
            if (hide){
                binding.btnAdd.visibility = View.GONE
                binding.btnMinus.visibility = View.GONE
                binding.tvPrice.visibility = View.VISIBLE
            }else{
                binding.btnAdd.visibility = View.VISIBLE
                binding.btnMinus.visibility = View.VISIBLE
                binding.tvPrice.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_order_product, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val product = dataSet[position]

        viewHolder.hideOrShowButtons(hideButtons)
        viewHolder.bindView(product)
        viewHolder.binding.btnAdd.setOnClickListener {
            onClick?.invoke(product, ProductActionListener.ADD_PRODUCT)
        }
        viewHolder.binding.btnMinus.setOnClickListener {
            onClick?.invoke(product, ProductActionListener.REMOVE_PRODUCT)
        }
    }

    override fun getItemCount() = dataSet.size

}
