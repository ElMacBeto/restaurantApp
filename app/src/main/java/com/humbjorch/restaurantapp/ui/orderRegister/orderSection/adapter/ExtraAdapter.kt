package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.ExtraModel
import com.humbjorch.restaurantapp.databinding.ItemExtraBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ExtrasDiffUtil

class ExtraAdapter(
    private var dataSet: List<ExtraModel>,
    private val onClick: () -> Unit
) :
    RecyclerView.Adapter<ExtraAdapter.ViewHolder>() {

    private val selectedPositions = mutableListOf<Int>()

    fun clearCheckBoxes() {
        selectedPositions.clear()
        notifyDataSetChanged()
    }

    fun updateList(newList: List<ExtraModel>) {
        val projectDiffUtil = ExtrasDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemExtraBinding.bind(view)

        fun bindView(productType: String) {
            binding.chSelected.text = productType
            binding.chSelected.isChecked = false
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_ingredient, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val extra = dataSet[position]

        viewHolder.bindView(extra.name)

        viewHolder.binding.chSelected.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPositions.add(viewHolder.adapterPosition)
            } else {
                selectedPositions.remove(viewHolder.adapterPosition)
            }
            onClick.invoke()
        }
    }

    fun getExtras(): List<ExtraModel> {
        var tempList = listOf<ExtraModel>()
        if (dataSet.isNotEmpty()) {
            selectedPositions.forEach { selectedPosition ->
                tempList = tempList.plus(dataSet[selectedPosition])
            }
        }
        if (tempList.isEmpty() && dataSet.isNotEmpty())
            tempList = tempList.plus(dataSet[0])
        return tempList
    }

    override fun getItemCount() = dataSet.size

}

