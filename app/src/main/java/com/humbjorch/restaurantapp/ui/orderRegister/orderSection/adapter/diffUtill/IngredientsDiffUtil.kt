package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.ProductsModel

class IngredientsDiffUtil(
    private val oldList: List<String>,
    private val newList: List<String>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition] == newList[newItemPosition] )
    }

}