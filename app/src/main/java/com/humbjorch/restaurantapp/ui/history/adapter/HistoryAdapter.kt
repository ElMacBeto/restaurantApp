package com.humbjorch.restaurantapp.ui.history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Tools.getTotal
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.ItemHistoryBinding
import com.humbjorch.restaurantapp.ui.home.adapter.OrderDiffUtil

class HistoryAdapter(
    private var dataSet: List<OrderModel>,
    private val onClick: (OrderModel) -> Unit
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun updateList(newList: List<OrderModel>) {
        val projectDiffUtil = OrderDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ctx = view.context
        val binding = ItemHistoryBinding.bind(view)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindView(order: OrderModel, isSelected: Boolean) {
            binding.order = order

            if (isSelected) {
                binding.container.setBackgroundColor(ctx.getColor(R.color.blue2))
            } else {
                binding.container.setBackgroundColor(ctx.getColor(R.color.white))
            }

            binding.tvTotal.text = getTotal(order).toString()

            val color:Int
            val statusDrawable:Int

            when(order.status){
                OrderStatus.PAID.value,
                OrderStatus.CASH_PAYMENT.value,
                OrderStatus.CARD_PAYMENT.value ->{
                    color = R.color.green1
                    statusDrawable = R.drawable.ic_pay
                }
                OrderStatus.WITHOUT_PAYING.value->{
                    color = R.color.black
                    statusDrawable = R.drawable.ic_pending
                }
                OrderStatus.CANCEL.value->{
                    color = R.color.red1
                    statusDrawable = R.drawable.ic_cancel
                }
                else -> {
                    color = R.color.black
                    statusDrawable = R.drawable.ic_pending
                }
            }

            binding.imgStatus.apply {
                setColorFilter(ctx.getColor(color))
                setImageDrawable(ctx.getDrawable(statusDrawable))
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_history, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val table = dataSet[position]
        val isSelected = viewHolder.adapterPosition == selectedPosition

        viewHolder.bindView(table, isSelected)
        viewHolder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = viewHolder.adapterPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
        viewHolder.binding.btnPrintOrder.setOnClickListener {
            onClick.invoke(table)
        }
    }

    override fun getItemCount() = dataSet.size

}
