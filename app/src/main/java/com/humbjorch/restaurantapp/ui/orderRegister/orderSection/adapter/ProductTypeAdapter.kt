package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.ProductsModel
import com.humbjorch.restaurantapp.databinding.ItemProductTypeBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ProductTypeDiffUtil

class ProductTypeAdapter(
    private var dataSet: List<ProductsModel>,
    private val onClick: (ProductsModel) -> Unit
) :
    RecyclerView.Adapter<ProductTypeAdapter.ViewHolder>() {


    private var selectedPosition = 0

    fun updateList(newList: List<ProductsModel>) {
        val projectDiffUtil = ProductTypeDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    fun clearSelectedPosition(){
        selectedPosition = 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemProductTypeBinding.bind(view)

        fun bindView(productType: ProductsModel, isSelected: Boolean) {
            binding.rbSelected.text = productType.name
            binding.rbSelected.isChecked = isSelected
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
        val isSelected = viewHolder.adapterPosition == selectedPosition

        viewHolder.bindView(productType, isSelected)

        if (isSelected){
            onClick.invoke(productType)
        }

        viewHolder.binding.rbSelected.isChecked = position == selectedPosition
        viewHolder.binding.rbSelected.setOnClickListener {
            selectedPosition = viewHolder.adapterPosition
            notifyDataSetChanged()
            onClick.invoke(productType)
        }
    }

    override fun getItemCount() = dataSet.size

}