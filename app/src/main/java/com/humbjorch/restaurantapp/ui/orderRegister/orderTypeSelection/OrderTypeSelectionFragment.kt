package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderTypeSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter.TableAdapter


class OrderTypeSelectionFragment : Fragment() {

    private val viewModel: OrderTypeSelectionViewModel by viewModels()
    private lateinit var binding: FragmentOrderTypeSelectionBinding
    private lateinit var adapter: TableAdapter
    private var tablePosition = -1

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
    }

    private fun setListeners() {
        binding.btnNext.setOnClickListener {
           navigateOrderSelection()
        }
        binding.swDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tvLabelTableSelection.visibility = View.INVISIBLE
                binding.rvTables.visibility = View.INVISIBLE
                binding.lottieDelivery.visibility = View.VISIBLE
            } else {
                binding.tvLabelTableSelection.visibility = View.VISIBLE
                binding.rvTables.visibility = View.VISIBLE
                binding.lottieDelivery.visibility = View.GONE
            }
        }
    }

    private fun navigateOrderSelection() {
        if (binding.swDelivery.isChecked) {
            val action =
                OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragmentToOrderSectionFragment(
                    tablePosition = -1,
                    order = null
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
                    order = null
                )
                viewModel.updateTableList(tablePosition)
                findNavController().navigate(action)
            }
        }
    }

    private fun setAdapter() {
        val tableList = App.tablesAvailable
        adapter = TableAdapter(tableList) {
            updatePositionSelected(it)
        }
        binding.rvTables.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvTables.adapter = adapter
    }

    private fun updatePositionSelected(table: TableAvailableModel) {
        tablePosition = table.position.toInt()
    }

    override fun onResume() {
        super.onResume()
        tablePosition = -1
        binding.swDelivery.isChecked = false
    }


}