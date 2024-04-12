package com.humbjorch.restaurantapp.ui.history

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools.getCurrentDate
import com.humbjorch.restaurantapp.core.utils.Tools.getDateForHistory
import com.humbjorch.restaurantapp.core.utils.Tools.getDateFormatted
import com.humbjorch.restaurantapp.core.utils.Tools.getTotalForList
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.databinding.FragmentHistoryBinding
import com.humbjorch.restaurantapp.ui.MainActivity
import com.humbjorch.restaurantapp.ui.history.adapter.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var orderAdapter: HistoryAdapter
    private var orderList: List<OrderModel> = emptyList()
    private var orderSelected: OrderModel? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private var startDate = getCurrentDate()
    @RequiresApi(Build.VERSION_CODES.O)
    private var endDate = getCurrentDate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setObservers()
        setListeners()
        setView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setView() {
        binding.tiStartDate.editText!!.setText(getDateForHistory())
        binding.tiEndDate.editText!!.setText(getDateForHistory())
        viewModel.getAllDayOrders(startDate, endDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.tiStartDate.editText!!.setOnClickListener{
            showDatePickerDialog{ dayOfMonth, monthOfYear, year ->
                val selectedDate = getDateFormatted(dayOfMonth, monthOfYear, year)
                startDate = getDateFormatted(dayOfMonth, monthOfYear, year, true)
                viewModel.getAllDayOrders(startDate, endDate)
                binding.tiStartDate.editText!!.setText(selectedDate)
            }
        }
        binding.tiEndDate.editText!!.setOnClickListener{
            showDatePickerDialog{dayOfMonth, monthOfYear, year ->
                val selectedDate = getDateFormatted(dayOfMonth, monthOfYear, year)
                endDate = getDateFormatted(dayOfMonth, monthOfYear, year, true)
                viewModel.getAllDayOrders(startDate, endDate)
                binding.tiEndDate.editText!!.setText(selectedDate)
            }
        }
    }

    private fun setObservers() {
        viewModel.getAllOrdersLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    orderList = it.data ?: emptyList()
                    orderAdapter.updateList(orderList)
                    setTotal()
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
        viewModel.printLiveData.observe(viewLifecycleOwner){
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

    private fun setTotal() {
        binding.tvTotal.text = getTotalForList(orderList).toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAdapter(){
        orderAdapter = HistoryAdapter(orderList){
            orderSelected = it
            viewModel.printTicket(it)
        }
        binding.rvTableOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun showDatePickerDialog(action:(Int, Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                action(dayOfMonth,monthOfYear,year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}