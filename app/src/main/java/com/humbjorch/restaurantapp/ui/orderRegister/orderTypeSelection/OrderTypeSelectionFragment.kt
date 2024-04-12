package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.showHide
import com.humbjorch.restaurantapp.core.utils.showOrInvisible
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderTypeSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.OrderSectionFragmentArgs
import com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter.TableAdapter


class OrderTypeSelectionFragment : Fragment() {

    private val viewModel: OrderTypeSelectionViewModel by viewModels()
    private lateinit var binding: FragmentOrderTypeSelectionBinding
    private lateinit var adapter: TableAdapter
    private val args: OrderTypeSelectionFragmentArgs by navArgs()
    private var tablePosition = 0
    private lateinit var orderModel: OrderModel
    private var productsOrder = listOf<ProductsOrderModel>()
    private var fromEdict = false
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tablePosition = args.tablePosition
        orderModel = args.order ?: OrderModel()
        productsOrder = orderModel.productList
        fromEdict = args.isEdict
        address = args.address ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderTypeSelectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListeners()
        if (tablePosition < 0)
            setView()
    }

    private fun setView() {
        binding.swDelivery.isChecked = true
        binding.tvLabelTableSelection.visibility = View.GONE
        binding.rvTables.visibility = View.INVISIBLE
        binding.lottieDelivery.visibility = View.VISIBLE
        binding.tiAddress.visibility = View.VISIBLE
        binding.tiAddress.editText!!.setText(address)
    }

    private fun setListeners() {
        binding.btnNext.setOnClickListener {
           navigateOrderSelection()
        }
        binding.swDelivery.setOnCheckedChangeListener { _, isChecked ->
            setDeliveryView(isChecked)
        }
    }

    private fun navigateOrderSelection() {
        if (binding.swDelivery.isChecked) {
            val action =
                OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragmentToOrderSectionFragment(
                    tablePosition = -1,
                    order = orderModel,
                    address = binding.tiAddress.editText!!.text.toString(),
                    isEdict = fromEdict
                )
            findNavController().navigate(action)
        } else {
            if (tablePosition < 0) {
                CustomToastWidget.show(
                    activity = requireActivity(),
                    message = getString(R.string.message_without_table),
                    type = TypeToast.INFORMATION
                )
            } else {
                val action = OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragmentToOrderSectionFragment(
                    tablePosition = tablePosition,
                    order = orderModel,
                    isEdict = true,
                    address = null
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun setAdapter() {
        val tableList = App.tablesAvailable
        adapter = TableAdapter(tableList) {
            updatePositionSelected(it)
        }
        adapter.setSelectedPosition(tablePosition)
        binding.rvTables.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvTables.adapter = adapter
    }

    private fun updatePositionSelected(table: TableAvailableModel) {
        tablePosition = table.position.toInt()
    }

    private fun setDeliveryView(isDelivery: Boolean){
        binding.tvLabelTableSelection.showHide(!isDelivery)
        binding.rvTables.showOrInvisible(!isDelivery)
        binding.lottieDelivery.showHide(isDelivery)
        binding.tiAddress.showHide(isDelivery)
    }

}