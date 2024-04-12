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
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools.getTotal
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.showHide
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.FragmentHomeBinding
import com.humbjorch.restaurantapp.ui.MainActivity
import com.humbjorch.restaurantapp.ui.home.adapter.OrderAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsOrderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tableOrderAdapter: OrderAdapter
    private lateinit var orderAdapter: ProductsOrderAdapter
    private var orderList = emptyList<OrderModel>()
    private var orderSelected = OrderModel()
    private var updateType = OrderStatus.WITHOUT_PAYING.value

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
        initView()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        viewModel.setPrinterSettings()
        (activity as MainActivity).showLateralNavigation(true)
        viewModel.getCurrentDayOrders()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        viewModel.getAllOrdersLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    val orders = it.data ?: OrderListModel()
                    App.ordersList = orders

                    orderList = if ((activity as MainActivity).binding.swDelivery.isChecked)
                        viewModel.getDeliveryOrders()
                    else
                        viewModel.getTableOrders()

                    tableOrderAdapter.updateList(orderList)
                    orderSelected = if (orderList.isNotEmpty()) orderList[0] else OrderModel()
                    orderAdapter.updateList(orderSelected.productList)
                    binding.lottieEmpty.showHide(orderSelected.productList.isEmpty())
                    setView()
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
        viewModel.updateOrderLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    if (updateType != OrderStatus.CANCEL.value)
                        viewModel.printTicket(orderSelected)
                    viewModel.setAllProducts()
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
        viewModel.setProductsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    viewModel.getCurrentDayOrders()
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
        viewModel.printLiveData.observe(viewLifecycleOwner) {
            when (it.status) {

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
                else -> {}
            }
        }
    }

    private fun setView() {
        val total = getTotal(orderSelected)
        orderSelected.total = total.toString()
        binding.tvTotal.text = getString(R.string.label_price_product, total)
        binding.tvTableEmpty.showHide(orderList.isEmpty())
    }

    private fun initAdapters() {
        orderList = viewModel.getTableOrders()
        tableOrderAdapter = OrderAdapter(orderList) {
            orderSelected = it
            orderAdapter.updateList(orderSelected.productList)
            binding.lottieEmpty.showHide(orderSelected.productList.isEmpty())
            setView()
        }
        binding.rvTableOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTableOrders.adapter = tableOrderAdapter
        // order adapter
        orderSelected = OrderModel()
        orderAdapter = ProductsOrderAdapter(orderSelected.productList)
        orderAdapter.hideButtons = true
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = orderAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.btnAddOrder.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToOrderTypeSelectionFragment(
                order = null,
                tablePosition = 0
            )
            findNavController().navigate(action)
        }
        binding.btnEditOrder.setOnClickListener {
            validateOrder {
                val action = if (orderSelected.table.toInt() < 0) {
                    HomeFragmentDirections.actionHomeFragmentToOrderTypeSelectionFragment(
                        tablePosition = orderSelected.table.toInt(),
                        order = orderSelected,
                        isEdict = true,
                        address = orderSelected.address
                    )
                } else {
                    HomeFragmentDirections.actionHomeFragmentToOrderSectionFragment(
                        tablePosition = orderSelected.table.toInt(),
                        order = orderSelected,
                        isEdict = true,
                        address = orderSelected.address
                    )
                }

                findNavController().navigate(action)
            }
        }
        binding.btnTakeOrder.setOnClickListener {
            validateOrder {
                (activity as MainActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_pay_order),
                    descriptionAlert = getString(R.string.dialog_description_pay_order),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = {
                        updateType = OrderStatus.PAID.value
                        viewModel.changePaidOrder(orderSelected)
                    },
                    buttonNegativeAction = { }
                )
            }
        }
        binding.btnPrintOrder.setOnClickListener {
            validateOrder {
                (activity as MainActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_printer_ticket),
                    descriptionAlert = getString(R.string.dialog_description_printer_ticket),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = { viewModel.printOrder(orderSelected) },
                    buttonNegativeAction = { }
                )
            }
        }
        binding.btnCancelOrder.setOnClickListener {
            validateOrder {
                (activity as MainActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_cancel_order),
                    descriptionAlert = getString(R.string.dialog_description_cancel_order),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = {
                        updateType = OrderStatus.CANCEL.value
                        viewModel.cancelOrder(orderSelected)
                    },
                    buttonNegativeAction = { }
                )
            }
        }

        (activity as MainActivity).binding.swDelivery.setOnCheckedChangeListener { _, isChecked ->
            orderList = if (isChecked) {
                binding.tvLabelOrderTitle.text = getString(R.string.label_delivery_orders)
                viewModel.getDeliveryOrders()
            } else {
                binding.tvLabelOrderTitle.text = getString(R.string.label_table_orders)
                viewModel.getTableOrders()
            }
            tableOrderAdapter.updateList(orderList)
            orderSelected = if (orderList.isNotEmpty()) orderList[0] else OrderModel()
            orderAdapter.updateList(orderSelected.productList)
            binding.lottieEmpty.showHide(orderSelected.productList.isEmpty())
            setView()
        }
        (activity as MainActivity).binding.btnRefresh.setOnClickListener {
            viewModel.getCurrentDayOrders()
        }
    }

    private fun validateOrder(action: () -> Unit) {
        if (orderSelected.productList.isEmpty()) {
            CustomToastWidget.show(
                activity = requireActivity(),
                message = getString(R.string.label_order_not_selected),
                type = TypeToast.INFORMATION
            )
        } else {
            action()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        viewModel.getCurrentDayOrders()
    }

}