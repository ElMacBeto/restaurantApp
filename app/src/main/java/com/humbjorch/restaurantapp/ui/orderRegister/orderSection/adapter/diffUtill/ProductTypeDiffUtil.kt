package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.ProductsModel

class ProductTypeDiffUtil(
    private val oldList: List<ProductsModel>,
    private val newList: List<ProductsModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].name == newList[newItemPosition].name &&
                oldList[oldItemPosition].description == newList[newItemPosition].description)
    }

}