package com.humbjorch.restaurantapp.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.genericAlert
import com.humbjorch.restaurantapp.core.utils.isVisible
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.FragmentNewHomeBinding
import com.humbjorch.restaurantapp.ui.home.adapter.OrderAdapter
import com.humbjorch.restaurantapp.ui.home.dialog.PaymentDialog
import com.humbjorch.restaurantapp.ui.orderRegister.OrderRegisterActivity
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsOrderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewHomeFragment : Fragment() {

    private val viewModel: NewHomeViewModel by viewModels()
    private lateinit var binding: FragmentNewHomeBinding
    private lateinit var tableOrderAdapter: OrderAdapter
    private lateinit var orderAdapter: ProductsOrderAdapter
    private var orderList = emptyList<OrderModel>()
    private var orderSelected = OrderModel()
    private var updateType = OrderStatus.WITHOUT_PAYING.value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListeners()
        setObservers()
        setView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireActivity(), OrderRegisterActivity::class.java))
        }
        binding.swDelivery.setOnCheckedChangeListener { _, isChecked ->
            orderList = if (isChecked) {
                viewModel.getDeliveryOrders()
            } else {
                viewModel.getTableOrders()
            }
            tableOrderAdapter.updateList(orderList)
            orderSelected = if (orderList.isNotEmpty()) orderList[0] else OrderModel()
            binding.tvTableEmpty.isVisible(orderList.isEmpty())
        }
        binding.nvDrawer.btnEditOrder.setOnClickListener {
            validateOrder {
                binding.lyDrawer.closeDrawer(GravityCompat.END)
                val intent = Intent(requireContext(), OrderRegisterActivity::class.java).apply {
                    putExtra(OrderRegisterActivity.ORDER_EXTRA_KEY, orderSelected)
                }
                startActivity(intent)
            }
        }
        binding.nvDrawer.btnPrintOrder.setOnClickListener {
            validateOrder {
                (activity as NewHomeActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_printer_ticket),
                    descriptionAlert = getString(R.string.dialog_description_printer_ticket),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = { viewModel.printOrder(orderSelected) },
                    buttonNegativeAction = { }
                )
            }
        }
        binding.nvDrawer.payOrderBtn.setOnClickListener {
            validateOrder {
                PaymentDialog {
                    updateType = it.value
                    viewModel.changePaidOrder(orderSelected, updateType)
                }.show(parentFragmentManager, PaymentDialog.TAG)
            }
        }
        binding.nvDrawer.btnCancelOrder.setOnClickListener {
            validateOrder {
                (activity as NewHomeActivity).genericAlert(
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        viewModel.getAllOrdersLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }
                Status.SUCCESS -> {
                    (activity as NewHomeActivity).dismissLoader()
                    val orders = it.data ?: OrderListModel()
                    updateOrderList(orders)
                }
                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
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
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
                else -> {}
            }
        }
        viewModel.updateOrderLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }
                Status.SUCCESS -> {
                    if (updateType != OrderStatus.CANCEL.value)
                        viewModel.printTicket(orderSelected)
                    binding.lyDrawer.closeDrawer(GravityCompat.END)
                    viewModel.setAllProducts()
                }
                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
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
                    (activity as NewHomeActivity).showLoader()
                }
                Status.SUCCESS -> {
                    (activity as NewHomeActivity).dismissLoader()
                    viewModel.getCurrentDayOrders()
                }
                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
        viewModel.cleanTablesLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }
                Status.SUCCESS -> {
                    (activity as NewHomeActivity).dismissLoader()
                }
                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
    }

    private fun initAdapter() {
        orderList = viewModel.getTableOrders()
        tableOrderAdapter = OrderAdapter(orderList) {
            orderSelected = it
            orderAdapter.updateList(orderSelected.productList)
            binding.lyDrawer.openDrawer(GravityCompat.END)
            val total = Tools.getTotal(orderSelected)
            orderSelected.total = total.toString()
            binding.nvDrawer.tvTotal.text = getString(R.string.label_price_product, total)
        }
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = tableOrderAdapter
        // order adapter
        orderSelected = OrderModel()
        orderAdapter = ProductsOrderAdapter(orderSelected.productList)
        orderAdapter.hideButtons = true
        binding.nvDrawer.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.nvDrawer.rvProductsOrder.adapter = orderAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setView() {
        binding.tvTableEmpty.isVisible(orderList.isEmpty())
        binding.lyDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.tvDate.text = Tools.getCurrentDate(true)
        viewModel.getCurrentDayOrders()
    }

    private fun updateOrderList(orders: OrderListModel) {
        App.ordersList = orders

        orderList = if (binding.swDelivery.isChecked)
            viewModel.getDeliveryOrders()
        else
            viewModel.getTableOrders()
        tableOrderAdapter.updateList(orderList)
        orderSelected = if (orderList.isNotEmpty()) orderList[0] else OrderModel()
        val emptyList = orderList.isEmpty()
        binding.tvTableEmpty.isVisible(emptyList)
        if (emptyList)
            viewModel.cleanTableAvailable()
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