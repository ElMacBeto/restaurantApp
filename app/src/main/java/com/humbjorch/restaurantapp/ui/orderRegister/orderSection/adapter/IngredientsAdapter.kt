package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.databinding.ItemIngredientBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.IngredientsDiffUtil

class IngredientsAdapter(
    private var dataSet: List<String>,
    private val onClick: () -> Unit
) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private val selectedPositions = mutableListOf(0)

    fun updateList(newList: List<String>) {
        val projectDiffUtil = IngredientsDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    fun clearCheckBoxes(){
        selectedPositions.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemIngredientBinding.bind(view)

        fun bindView(productType: String) {
            binding.chSelected.text = productType
            binding.chSelected.isChecked = false
        }
        fun setChecked(boolean: Boolean){
            binding.chSelected.isChecked = boolean
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_ingredient, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val productType = dataSet[position]

        viewHolder.bindView(productType)
        val isIngredientChecked = viewHolder.adapterPosition == 0
        viewHolder.setChecked(isIngredientChecked)

        viewHolder.binding.chSelected.setOnCheckedChangeListener { _,isChecked  ->
            if (isChecked){
                selectedPositions.add(viewHolder.adapterPosition)
            }else{
                selectedPositions.remove(viewHolder.adapterPosition)
            }
            onClick.invoke()
        }
    }

    fun getIngredients():List<String>{
        var tempList = listOf<String>()
        if (dataSet.isNotEmpty()){
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

