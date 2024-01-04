package com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.data.model.TableAvailableModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderTypeSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderTypeSelection.adapter.TableAdapter


class OrderTypeSelectionFragment : Fragment() {

    private lateinit var binding: FragmentOrderTypeSelectionBinding
    private lateinit var adapter: TableAdapter
    private var tablePosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            if (binding.swDelivery.isChecked){
                val action = OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragmentToOrderSectionFragment(
                    tablePosition = -1
                )
                findNavController().navigate(action)
            }else{
                if (tablePosition<0){
                    Toast.makeText(requireContext(), "Selecciona una meza", Toast.LENGTH_SHORT).show()
                }else{
                    val action = OrderTypeSelectionFragmentDirections.actionOrderTypeSelectionFragmentToOrderSectionFragment(
                        tablePosition = tablePosition
                    )
                    updateTableList()
                    findNavController().navigate(action)
                }
            }
        }
        binding.swDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.viewNoSelected.visibility = View.VISIBLE
            }else{
                binding.viewNoSelected.visibility = View.GONE
            }
        }
    }

    private fun updateTableList() {
        App.tablesAvailable.onEach {
            if(it.position == tablePosition.toString()){
                it.available = false
            }
        }
    }

    private fun setAdapter() {
        val tableList = App.tablesAvailable
        adapter = TableAdapter(tableList){
            updatePositionSelected(it)
        }
        binding.rvTables.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvTables.adapter=adapter
    }

    private fun updatePositionSelected(table: TableAvailableModel){
        tablePosition = table.position.toInt()
    }

    override fun onResume() {
        super.onResume()
        tablePosition = -1
        binding.swDelivery.isChecked = false
    }


}