package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.isVisible
import com.humbjorch.restaurantapp.core.utils.showOrInvisible
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderTypeSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.RegisterOrderViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter.TableAdapter


class OrderTypeSelectionFragment : Fragment() {

    private val activityViewModel: RegisterOrderViewModel by activityViewModels()
    private lateinit var binding: FragmentOrderTypeSelectionBinding
    private lateinit var adapter: TableAdapter
    private var tablePosition = 0
    private var address: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderTypeSelectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tablePosition = activityViewModel.tableSelected.value ?: 0
        address = activityViewModel.orderAddress.value ?: ""

        setAdapter()
        setListeners()
        if (tablePosition < 0) setView()
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
        validateTable {
            val action =
                OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragment2ToNewOrderSelectionFragment()
            findNavController().navigate(action)
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

    private fun setDeliveryView(isDelivery: Boolean) {
        binding.tvLabelTableSelection.isVisible(!isDelivery)
        binding.rvTables.showOrInvisible(!isDelivery)
        binding.lottieDelivery.isVisible(isDelivery)
        binding.tiAddress.isVisible(isDelivery)
    }

    private fun validateTable(action: () -> Unit) {
        if (tablePosition < 0 && !binding.swDelivery.isChecked) {
            CustomToastWidget.show(
                activity = requireActivity(),
                message = getString(R.string.message_without_table),
                type = TypeToast.INFORMATION
            )
        } else {
            action.invoke()
        }
    }

}