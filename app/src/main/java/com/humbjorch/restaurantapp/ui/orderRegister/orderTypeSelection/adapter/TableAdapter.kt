package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.databinding.ItemTableBinding

class TableAdapter(
    private var dataSet: List<TableAvailableModel>,
    private val onClick: (TableAvailableModel) -> Unit
) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {


    fun updateList(newList: List<TableAvailableModel>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ctx = view.context
        private val binding = ItemTableBinding.bind(view)

        fun bindView(table: TableAvailableModel) {
            binding.tvPosition.text = table.position
            binding.tvPosition.isEnabled = table.available
            if (table.isClicked){
                binding.container.setBackgroundColor(ctx.getColor(R.color.gray2))
            }else{
                binding.container.setBackgroundColor(ctx.getColor(R.color.green1))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_table, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val table = dataSet[position]

        viewHolder.bindView(table)
        viewHolder.itemView.setOnClickListener {
            onClick.invoke(table)
        }
    }

    override fun getItemCount() = dataSet.size

}
