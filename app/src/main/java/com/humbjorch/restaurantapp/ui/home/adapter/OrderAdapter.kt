package com.humbjorch.restaurantapp.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.ItemNewOrderBinding

class OrderAdapter(
    private var dataSet: List<OrderModel>,
    private val onClick: (OrderModel) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private var selectedPosition = 0


    fun updateList(newList: List<OrderModel>) {
        val projectDiffUtil = OrderDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ctx = view.context
        private val binding = ItemNewOrderBinding.bind(view)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindView(order: OrderModel, isSelected: Boolean) {
            binding.order = order

            if (isSelected) {
                binding.container.setBackgroundColor(ctx.getColor(R.color.blue2))
            } else {
                binding.container.setBackgroundColor(ctx.getColor(R.color.white))
            }

            if (order.table.toInt() > -1) {
                binding.tvTableNumber.text = order.table
                binding.imgOrderType.setImageDrawable(ctx.getDrawable(R.drawable.ic_table))
            } else {
                binding.tvTableNumber.text = ""
                binding.imgOrderType.setImageDrawable(ctx.getDrawable(R.drawable.ic_delivery))
            }
            binding.tvOrderNumber.text = order.orderNumber.toString()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_new_order, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val table = dataSet[position]
        val isSelected = viewHolder.adapterPosition == selectedPosition

        viewHolder.bindView(table, isSelected)
        viewHolder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = viewHolder.adapterPosition
            onClick.invoke(table)
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount() = dataSet.size

}
