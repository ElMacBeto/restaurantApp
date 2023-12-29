package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.TableAvailableModel

class TableDiffUtil(
    private val oldList: List<TableAvailableModel>,
    private val newList: List<TableAvailableModel>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].position == newList[newItemPosition].position
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].position ==  newList[newItemPosition].position) &&
                (oldList[oldItemPosition].available ==  newList[newItemPosition].available)
    }

}