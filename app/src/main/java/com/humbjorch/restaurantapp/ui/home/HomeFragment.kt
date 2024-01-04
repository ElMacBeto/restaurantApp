package com.humbjorch.restaurantapp.ui.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.FragmentHomeBinding
import com.humbjorch.restaurantapp.ui.MainActivity
import com.humbjorch.restaurantapp.ui.home.adapter.OrderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tableOrderAdapter: OrderAdapter
    private lateinit var deliveryOrderAdapter: OrderAdapter
    private var tableOrders = emptyList<OrderModel>()
    private var deliveryOrders = emptyList<OrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initAdapters()
        setObservers()
        viewModel.getAllOrders()
    }

    private fun setObservers() {
        viewModel.getAllOrdersLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    App.ordersList = it.data ?: OrderListModel()
                    tableOrders = viewModel.getTableOrders()
                    tableOrderAdapter.updateList(tableOrders)
                    deliveryOrders = viewModel.getDeliveryOrders()
                    deliveryOrderAdapter.updateList(deliveryOrders)
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initAdapters() {
        tableOrders = viewModel.getTableOrders()
        tableOrderAdapter = OrderAdapter(tableOrders) {

        }
        binding.rvTableOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTableOrders.adapter = tableOrderAdapter

        deliveryOrders = viewModel.getDeliveryOrders()
        deliveryOrderAdapter = OrderAdapter(deliveryOrders) {

        }
        binding.rvDeliveryOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeliveryOrders.adapter = deliveryOrderAdapter
    }

    private fun setListeners() {
        binding.btnAddOrder.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_orderTypeSelectionFragment)
        }
    }

    override fun onResume() {
        super.onResume()

    }

}