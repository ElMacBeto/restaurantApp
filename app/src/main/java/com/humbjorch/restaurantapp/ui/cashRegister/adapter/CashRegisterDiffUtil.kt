package com.humbjorch.restaurantapp.ui.cashRegister.adapter

import androidx.recyclerview.widget.DiffUtil
import com.humbjorch.restaurantapp.data.model.CashRegisterModel

class CashRegisterDiffUtil(
    private val oldList: List<CashRegisterModel>,
    private val newList: List<CashRegisterModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].name == newList[newItemPosition].name)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}