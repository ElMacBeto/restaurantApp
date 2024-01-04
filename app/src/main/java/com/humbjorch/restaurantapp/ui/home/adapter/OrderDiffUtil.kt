package com.humbjorch.restaurantapp.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.OrderModel

class OrderDiffUtil(
    private val oldList: List<OrderModel>,
    private val newList: List<OrderModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].id == newList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}