package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel

class ProductsOrderDiffUtil(
    private val oldList: List<ProductsOrderModel>,
    private val newList: List<ProductsOrderModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].product == newList[newItemPosition].product &&
                oldList[oldItemPosition].ingredients == newList[newItemPosition].ingredients)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}