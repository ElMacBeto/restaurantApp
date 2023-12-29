package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.databinding.ItemProductTypeBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ProductTypeDiffUtil

class ProductTypeAdapter(
    private var dataSet: List<String>,
    private val onClick: (String) -> Unit
) :
    RecyclerView.Adapter<ProductTypeAdapter.ViewHolder>() {


    fun updateList(newList: List<String>) {
        val projectDiffUtil = ProductTypeDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemProductTypeBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(productType: String) {
            binding.tvProductType.text = productType
        }


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_product_type, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val productType = dataSet[position]

        viewHolder.bindView(productType)
        viewHolder.itemView.setOnClickListener {
            onClick.invoke(productType)
        }
    }

    override fun getItemCount() = dataSet.size

}