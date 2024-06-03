package com.humbjorch.restaurantapp.ui.cashRegister.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.data.model.CashRegisterModel
import com.humbjorch.restaurantapp.databinding.ItemCashRegisterBinding

class CashRegisterAdapter(
    private var dataSet: List<CashRegisterModel>,
    private val onClickEdit: (CashRegisterModel) -> Unit,
    private val onClickDelete: (CashRegisterModel) -> Unit
) :
    RecyclerView.Adapter<CashRegisterAdapter.ViewHolder>() {

    fun updateList(newList: List<CashRegisterModel>) {
        val projectDiffUtil = CashRegisterDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemCashRegisterBinding.bind(view)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindView(cashRegister: CashRegisterModel) {
            binding.tvNameCashRegister.text = cashRegister.name
            binding.tvValueCashRegister.text =  Tools.formatMoney(cashRegister.value.toDouble())
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_cash_register, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]

        viewHolder.bindView(item)

        viewHolder.binding.btnEdit.setOnClickListener {
            onClickEdit.invoke(item)
        }
        viewHolder.itemView.setOnClickListener {
            onClickDelete.invoke(item)
        }
    }

    override fun getItemCount() = dataSet.size

}
