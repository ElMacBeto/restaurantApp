package com.humbjorch.restaurantapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Tools.getTotal
import com.humbjorch.restaurantapp.databinding.FragmentSummaryBinding


class SummaryFragment : Fragment() {

    private lateinit var binding: FragmentSummaryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTotal()
    }

    private fun setTotal() {
        val text = StringBuilder()
        text.append("$")
        text.append(getTotalOfDay())
        binding.tvSummaryTotal.text = text
    }

    private fun getTotalOfDay(): String {
        var orderTotal = 0
        App.ordersList.orders.forEach { order ->

            if (order.status == OrderStatus.PAID.value)
                orderTotal += getTotal(order)
        }
        return orderTotal.toString()
    }

}