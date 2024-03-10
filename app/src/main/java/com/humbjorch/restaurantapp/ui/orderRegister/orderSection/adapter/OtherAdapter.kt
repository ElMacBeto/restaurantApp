package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.ExtraModel
import com.humbjorch.restaurantapp.databinding.ItemRadioBtnBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.ExtrasDiffUtil
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.diffUtill.StringDiffUtil

class OtherAdapter(
    private var dataSet: List<String>,
    private val onClick: () -> Unit
) :
    RecyclerView.Adapter<OtherAdapter.ViewHolder>() {

    private var selectedPosition:Int = 0

    fun clearCheckBoxes() {
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun updateList(newList: List<String>) {
        val projectDiffUtil = StringDiffUtil(dataSet, newList)
        val result = DiffUtil.calculateDiff(projectDiffUtil)
        dataSet = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemRadioBtnBinding.bind(view)

        fun bindView(productType: String, isSelected: Boolean) {
            binding.rbSelected.text = productType
            binding.rbSelected.isChecked = isSelected
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_radio_btn, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = dataSet[position]
        val isSelected = selectedPosition == viewHolder.adapterPosition

        viewHolder.bindView(data, isSelected)

        viewHolder.binding.rbSelected.setOnClickListener {
            selectedPosition = viewHolder.adapterPosition
            notifyDataSetChanged()
            onClick.invoke()
        }
    }

    fun getOtherValue(): String {
        var other = ""
        if (dataSet.isNotEmpty()) {
            other = dataSet[selectedPosition]
        }
        return other
    }

    override fun getItemCount() = dataSet.size

}
